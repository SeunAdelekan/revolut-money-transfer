package com.iyanuadelekan.moneytransfer

import com.iyanuadelekan.moneytransfer.components.ResponseDispatcher
import com.iyanuadelekan.moneytransfer.constants.ErrorMessage
import com.iyanuadelekan.moneytransfer.constants.RequestError
import com.iyanuadelekan.moneytransfer.controllers.AccountController
import com.iyanuadelekan.moneytransfer.helpers.InsufficientBalanceException
import com.iyanuadelekan.moneytransfer.helpers.UnsupportedContentTypeException
import com.iyanuadelekan.moneytransfer.helpers.seedCurrencies
import com.iyanuadelekan.moneytransfer.helpers.seedExchangeRates
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
                    ApiBuilder.post("/deposits", AccountController.fundAccount)
                    ApiBuilder.post("/withdrawals", AccountController.withdrawAccount)
                    ApiBuilder.get("/transactions", AccountController.getAccountTransactions)
                    ApiBuilder.post("/transfers/:recipient_account_id", AccountController.transferFundsToAccount)
                }
            }
        }

        exception(UnsupportedContentTypeException::class.java) { _, ctx ->
            ResponseDispatcher.sendError(
                    ctx,
                    ErrorMessage.UNSUPPORTED_CONTENT_TYPE.message,
                    RequestError.UNSUPPORTED_CONTENT_TYPE.code,
                    415)
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