package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.InvalidParameterException
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.services.AccountService
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.CurrencyService
import com.iyanuadelekan.moneytransfer.services.CurrencyServiceImpl
import java.math.BigDecimal

class ParameterValidator {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()


    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateAccountCreationParams(ctx: Context): AccountData {

        return ctx.bodyValidator<AccountData>()
                .check({ it.accountName.length > 3 },
                        "Account name must be a minimum of 3 characters in length.")
                .check({ currencyService.currencyExists(it.currency ) },
                        "That currency is not supported at the moment")
                .get()
    }

    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateAccountRetrievalParams(ctx: Context): String {
        val accountId = ctx.pathParam<String>("account_id")
                .check({ accountService.verifyAccountRegistered(it) },
                        "An account with that ID does not exist.")
                .get()
        val account = accountService.getAccount(accountId)
        return account.id
    }

    @Throws(BadRequestResponse::class)
    fun validateListAccountParams(ctx: Context): Pair<Int, Int> {
        with (ctx) {
            val page = queryParam<Int>("page")
                    .check({ it > 0 }, "Page must be greater than 0")
                    .getOrNull() ?: 1
            val limit = queryParam<Int>("limit")
                    .check({ it > 0 },
                            "Limit must be greater than 0").getOrNull() ?: 50

            return Pair(page, limit)
        }
    }

    @Throws(BadRequestResponse::class)
    fun validateFundAccountParams(ctx: Context): Pair<String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                    "An account with that ID does not exist.")
                    .get()
            val transactionData = bodyValidator<TransactionOperationData>()
                    .check({ it.amount > BigDecimal.ZERO },
                            "Transaction amounts must be greater than 0.00")
                    .check({ currencyService.currencyExists(it.currency ) },
                            "That currency is not supported at the moment")
                    .get()
            return Pair(accountId, transactionData)
        }
    }

    @Throws(BadRequestResponse::class)
    fun validateFundTransferParams(ctx: Context): Triple<String, String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                            "A sender account with that ID does not exist.")
                    .get()
            val recipientAccountId = pathParam<String>("recipient_account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                    "A recipient account with that ID does not exist.")
                    .get()
            val transactionData = bodyValidator<TransactionOperationData>()
                    .check({ it.amount > BigDecimal.ZERO },
                            "Transaction amounts must be greater than 0.00")
                    .check({ currencyService.currencyExists(it.currency ) },
                            "That currency is not supported at the moment").get()

            return Triple(accountId, recipientAccountId, transactionData);
        }
    }

    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateTransactionRetrievalParams(ctx: Context): Triple<String, Int, Int> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                            "An account with that ID does not exist.")
                    .get()
            val page = queryParam<Int>("page")
                    .check({ it > 0 }, "page must be greater than 0")
                    .getOrNull() ?: 1
            val limit = queryParam<Int>("limit")
                    .check({ it > 0 },
                            "limit must be greater than 0").getOrNull() ?: 50
            return Triple(accountId, page, limit)
        }
    }
}