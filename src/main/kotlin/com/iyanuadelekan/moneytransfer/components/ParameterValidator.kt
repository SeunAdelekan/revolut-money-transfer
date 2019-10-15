package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.constants.ErrorMessage
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.services.AccountService
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.CurrencyService
import com.iyanuadelekan.moneytransfer.services.CurrencyServiceImpl
import io.javalin.core.validation.Validator
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import java.math.BigDecimal

class ParameterValidator {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()


    @Throws(BadRequestResponse::class)
    fun validateAccountCreationParams(ctx: Context): AccountData {
        lateinit var dataValidator: Validator<AccountData>
        try {
            dataValidator = ctx.bodyValidator()
        } catch (error: Exception) {
            throw BadRequestResponse(ErrorMessage.ACCOUNT_CREATION_PARAMS_REQUIRED.message)
        }
        return dataValidator
                .check({ it.accountName.length > 3 },
                        ErrorMessage.INVALID_ACCOUNT_NAME.message)
                .check({ currencyService.currencyExists(it.currency ) },
                        ErrorMessage.INVALID_CURRENCY_NAME.message)
                .get()
    }

    fun validateAccountRetrievalParams(ctx: Context): String {
        val accountId = ctx.pathParam<String>("account_id")
                .check({ accountService.verifyAccountRegistered(it) },
                        ErrorMessage.INVALID_ACCOUNT_ID.message)
                .get()
        val account = accountService.getAccount(accountId)
        return account.id
    }

    fun validateListAccountParams(ctx: Context): Pair<Int, Int> {
        with (ctx) {
            val page = queryParam<Int>("page")
                    .check({ it > 0 }, ErrorMessage.INVALID_PAGE.message)
                    .getOrNull() ?: 1
            val limit = queryParam<Int>("limit")
                    .check({ it > 0 },
                            ErrorMessage.INVALID_PAGE_LIMIT.message).getOrNull() ?: 50

            return Pair(page, limit)
        }
    }

    fun validateFundActionParams(ctx: Context): Pair<String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                    ErrorMessage.INVALID_ACCOUNT_ID.message)
                    .get()

            val transactionData = validateTransactionOperationData(ctx)

            return Pair(accountId, transactionData)
        }
    }

    fun validateTransferParams(ctx: Context): Triple<String, String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                            ErrorMessage.INVALID_SENDER_ACCOUNT_ID.message)
                    .get()
            val recipientAccountId = pathParam<String>("recipient_account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                    ErrorMessage.INVALID_RECIPIENT_ACCOUNT_ID.message)
                    .get()
            val transactionData = validateTransactionOperationData(ctx)

            return Triple(accountId, recipientAccountId, transactionData);
        }
    }

    fun validateTransactionRetrievalParams(ctx: Context): Triple<String, Int, Int> {
        with (ctx) {
            val accountId = pathParam<String>("account_id")
                    .check({ accountService.verifyAccountRegistered(it) },
                            ErrorMessage.INVALID_ACCOUNT_ID.message)
                    .get()
            val page = queryParam<Int>("page")
                    .check({ it > 0 }, ErrorMessage.INVALID_PAGE.message)
                    .getOrNull() ?: 1
            val limit = queryParam<Int>("limit")
                    .check({ it > 0 },
                            ErrorMessage.INVALID_PAGE_LIMIT.message).getOrNull() ?: 50
            return Triple(accountId, page, limit)
        }
    }

    @Throws(BadRequestResponse::class)
    private fun validateTransactionOperationData(ctx: Context): TransactionOperationData {
        lateinit var transactionDataValidator: Validator<TransactionOperationData>

        try {
            transactionDataValidator = ctx.bodyValidator()
        } catch (error: Exception) {
            throw BadRequestResponse(ErrorMessage.TRANSACTION_OPERATION_PARAMS_REQUIRED.message)
        }

        return transactionDataValidator.check({ it.amount > BigDecimal.ZERO },
                ErrorMessage.INVALID_TRANSACTION_AMOUNT.message)
                .check({ currencyService.currencyExists(it.currency ) },
                        ErrorMessage.INVALID_CURRENCY_NAME.message)
                .get()
    }
}