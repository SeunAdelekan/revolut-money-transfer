package components

import exceptions.InvalidParameterException
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

        if (accountName.length < 5) {
            throw BadRequestResponse("Invalid accountName")
        }
        if (!currencyService.currencyExists(currency)) {
            throw InvalidParameterException("Invalid currencyId $currency")
        }
        return  sanitizedAccountData;
    }

    @Throws(BadRequestResponse::class, InvalidParameterException::class)
    fun validateAccountRetrievalParams(ctx: Context): Long {
        val accountId = ctx.pathParam<Long>("account_id").get()
        val account = accountService.getAccount(accountId)
                ?: throw InvalidParameterException("An account with id $accountId does not exist.")
        return account.id
    }

    @Throws(BadRequestResponse::class)
    fun validateListAccountParams(ctx: Context): Pair<Int, Int> {
        val page = ctx.queryParam<Int>("page").check({ it > 0 }).getOrNull() ?: 1
        val limit = ctx.queryParam<Int>("limit").check({ it > 0 }).getOrNull() ?: 50

        return Pair(page, limit)
    }

    fun validateFundAccountParams(ctx: Context): Pair<Long, TransactionOperationData> {
        val accountId = ctx.pathParam<Long>("account_id").get()
        val transactionData = ctx.bodyValidator<TransactionOperationData>().get()
        return Pair(accountId, transactionData)
    }
}