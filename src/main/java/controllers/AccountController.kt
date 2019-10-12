package controllers

import components.ParameterValidator
import components.ResponseDispatcher
import io.javalin.http.Handler
import models.entities.Account
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
        val account = accountService.getAccount(accountId) as Account
        ResponseDispatcher.sendSuccess(ctx, account, 200)
    }

    internal val listAccounts = Handler { ctx ->
        val (page, limit) = parameterValidator.validateListAccountParams(ctx)
        val accounts = accountService.getAccounts(page, limit)
        ResponseDispatcher.sendSuccess(ctx, accounts, 200)
    }

    internal val getAccountTransactions = Handler {  }

    internal val fundAccount = Handler { ctx ->
        val (accountId, transactionData) = parameterValidator.validateFundAccountParams(ctx)
        val account = transactionService.processDeposit(accountId, transactionData)
        ResponseDispatcher.sendSuccess(ctx, account, 200)
    }

    internal val withdrawAccountFunds = Handler { ctx ->

    }

    internal val transferFundsToAccount = Handler { ctx ->
        val (sourceAccountId, recipientAccountId, transactionData) = parameterValidator.validateFundTransferParams(ctx)
        val transferData = transactionService.processTransfer(sourceAccountId, recipientAccountId, transactionData)
        ResponseDispatcher.sendSuccess(ctx, transferData, 200)
    }
}