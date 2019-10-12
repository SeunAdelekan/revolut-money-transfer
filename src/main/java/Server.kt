import components.ResponseDispatcher
import controllers.AccountController
import exceptions.InvalidParameterException
import exceptions.InvalidUserIdException
import exceptions.UnsupportedContentTypeException
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.*
import models.entities.Currency
import models.entities.ExchangeRate
import repositories.CurrencyRepositoryImpl
import repositories.ExchangeRateRepositoryImpl

fun seedDataStore() {
    val currencyRepository = CurrencyRepositoryImpl()
    val exchangeRateRepository = ExchangeRateRepositoryImpl()
    val naira = Currency("NGN")
    val pound = Currency("GBP")
    currencyRepository.save(naira)
    currencyRepository.save(pound)

    val nairaToPounds = ExchangeRate(naira, pound, 0.0022)
    val poundsToNaira = ExchangeRate(pound, naira, 456.66)
    exchangeRateRepository.save(nairaToPounds)
    exchangeRateRepository.save(poundsToNaira)
}


fun main() {
    seedDataStore()
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
                    ctx, "Unsupported HTTP content type", errorCode = "1001")
        }

        exception(InvalidUserIdException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx, "A user with id ${error.userId} does not exist.", "1000")
        }

        exception(InvalidParameterException::class.java) { error, ctx ->
            ResponseDispatcher.sendError(
                    ctx, error.message as String, "1000")
        }
    }
}