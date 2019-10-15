package com.iyanuadelekan.moneytransfer

import com.iyanuadelekan.moneytransfer.components.ResponseDispatcher
import com.iyanuadelekan.moneytransfer.controllers.AccountController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.BadRequestResponse

internal fun startApp(port: Int = 7000): Javalin {
    seedCurrencies()
    seedExchangeRates()

    val app = Javalin.create().start(port)

    with (app) {
        routes {
            before {
                if (it.method() == "POST" && it.contentType() != "application/json") {
                    throw UnsupportedContentTypeException(it.contentType())
                }
            }
            path("accounts") {
                post(AccountController.createAccount)
                get(AccountController.listAccounts)
                path(":account_id") {
                    get(AccountController.getAccount)
                    ApiBuilder.get("/transactions", AccountController.getAccountTransactions)
                    ApiBuilder.post("/deposits", AccountController.fundAccount)
                    ApiBuilder.post("/transfers/:recipient_account_id", AccountController.transferFundsToAccount)
                }
            }
        }

        exception(UnsupportedContentTypeException::class.java) { _, ctx ->
            ResponseDispatcher.sendError(
                    ctx,
                    "Unsupported HTTP content type",
                    RequestError.UNSUPPORTED_CONTENT_TYPE.code,
                    415)
        }

        exception(InvalidParameterException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx,
                    error.message as String,
                    RequestError.INVALID_PARAMETER.code)
        }

        exception(InsufficientBalanceException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx,
                    error.message as String,
                    RequestError.INSUFFICIENT_BALANCE.code)
        }

        exception(BadRequestResponse::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx,
                    error.message as String,
                    RequestError.INVALID_PARAMETER.code)
        }
    }
    return app
}

fun main() {
    startApp()
}