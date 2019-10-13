package components

import InvalidParameterException
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import models.AccountData
import models.TransactionOperationData
import services.*

class ParameterValidator {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()


    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateAccountCreationParams(ctx: Context): AccountData {
        val sanitizedAccountData = ctx.bodyValidator<AccountData>().get()
        val (accountName, currency) = sanitizedAccountData

        if (accountName.length < 3) {
            throw BadRequestResponse("Account name must be a minimum of 3 characters in length.")
        }
        if (!currencyService.currencyExists(currency)) {
            throw InvalidParameterException("Invalid currencyId $currency")
        }
        return  sanitizedAccountData;
    }

    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateAccountRetrievalParams(ctx: Context): String {
        val accountId = ctx.pathParam<String>("account_id").get()
        val account = accountService.getAccount(accountId)
        return account.id
    }

    @Throws(BadRequestResponse::class)
    fun validateListAccountParams(ctx: Context): Pair<Int, Int> {
        with (ctx) {
            val page = queryParam<Int>("page").check({ it > 0 }).getOrNull() ?: 1
            val limit = queryParam<Int>("limit").check({ it > 0 }).getOrNull() ?: 50

            return Pair(page, limit)
        }
    }

    @Throws(BadRequestResponse::class)
    fun validateFundAccountParams(ctx: Context): Pair<String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id").get()
            val transactionData = bodyValidator<TransactionOperationData>().get()
            return Pair(accountId, transactionData)
        }
    }

    @Throws(BadRequestResponse::class)
    fun validateFundTransferParams(ctx: Context): Triple<String, String, TransactionOperationData> {
        with (ctx) {
            val accountId = pathParam<String>("account_id").get()
            val recipientAccountId = pathParam<String>("recipient_account_id").get()
            val transactionData = bodyValidator<TransactionOperationData>().get()

            return Triple(accountId, recipientAccountId, transactionData);
        }
    }

    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateTransactionRetrievalParams(ctx: Context): Triple<String, Int, Int> {
        val accountId = ctx.pathParam<String>("account_id").get()
        val page = ctx.queryParam<Int>("page").check({ it > 0 }).getOrNull() ?: 1
        val limit = ctx.queryParam<Int>("limit").check({ it > 0 }).getOrNull() ?: 50
        return Triple(accountId, page, limit)
    }
}