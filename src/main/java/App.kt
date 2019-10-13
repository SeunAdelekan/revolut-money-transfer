import components.ResponseDispatcher
import controllers.AccountController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    seedCurrencies()
    seedExchangeRates()

    val app = Javalin.create().start(7000)

    with (app) {
        routes {
            before {
                if (it.contentType() != "application/json" && it.contentType() != null) {
                    throw UnsupportedContentTypeException(it.contentType() as String)
                }
            }
            path("accounts") {
                post(AccountController.createAccount)
                get(AccountController.listAccounts)
                path(":account_id") {
                    get(AccountController.getAccount)
                    ApiBuilder.get("/transactions", AccountController.getAccountTransactions)
                    ApiBuilder.post("/deposits", AccountController.fundAccount)
                    ApiBuilder.post("/withdrawals", AccountController.withdrawAccountFunds)
                    ApiBuilder.post("/transfers/:recipient_account_id", AccountController.transferFundsToAccount)
                }
            }
        }

        exception(UnsupportedContentTypeException::class.java) { error, ctx ->
            println(error.message)
            ResponseDispatcher.sendError(
                    ctx, "Unsupported HTTP content type", errorCode = "1000")
        }

        exception(InvalidUserIdException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx, "A user with id ${error.userId} does not exist.", "1001")
        }

        exception(InvalidParameterException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx, error.message as String, "1002")
        }

        exception(InsufficientBalanceException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx, error.message as String, "1003")
        }
    }
}