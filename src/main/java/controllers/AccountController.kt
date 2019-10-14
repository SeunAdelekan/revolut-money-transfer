package controllers

import components.ParameterValidator
import components.ResponseDispatcher
import io.javalin.http.Handler
import services.AccountService
import services.AccountServiceImpl
import services.TransactionService
import services.TransactionServiceImpl

internal object AccountController {

    private val parameterValidator = ParameterValidator()
    private val accountService: AccountService = AccountServiceImpl()
    private val transactionService: TransactionService = TransactionServiceImpl()

    internal val createAccount = Handler { ctx ->
        val accountData = parameterValidator.validateAccountCreationParams(ctx)
        val account = accountService.createBankAccount(accountData)
        ResponseDispatcher.sendSuccess(ctx, account, 201)
    }

    internal val getAccount = Handler { ctx ->
        val accountId = parameterValidator.validateAccountRetrievalParams(ctx)
        val account = accountService.getAccount(accountId)
        ResponseDispatcher.sendSuccess(ctx, account, 200)
    }

    internal val listAccounts = Handler { ctx ->
        val (page, limit) = parameterValidator.validateListAccountParams(ctx)
        val accounts = accountService.getAccounts(page, limit)
        ResponseDispatcher.sendSuccess(ctx, accounts, 200)
    }

    internal val getAccountTransactions = Handler { ctx ->
        val (accountId, page, limit) = parameterValidator.validateTransactionRetrievalParams(ctx)
        val transactions = accountService.getAccountTransactions(accountId, page, limit)
        ResponseDispatcher.sendSuccess(ctx, transactions, 200)
    }

    internal val fundAccount = Handler { ctx ->
        val (accountId, transactionData) = parameterValidator.validateFundAccountParams(ctx)
        val account = transactionService.processDeposit(accountId, transactionData)
        ResponseDispatcher.sendSuccess(ctx, account, 200)
    }

    internal val transferFundsToAccount = Handler { ctx ->
        val (sourceAccountId, recipientAccountId, transactionData) =
                parameterValidator.validateFundTransferParams(ctx)
        val transferData = transactionService.processTransfer(
                sourceAccountId,
                recipientAccountId,
                transactionData)
        ResponseDispatcher.sendSuccess(ctx, transferData, 200)
    }
}