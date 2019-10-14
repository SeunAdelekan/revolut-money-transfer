package com.iyanuadelekan.moneytransfer.controllers

import com.iyanuadelekan.moneytransfer.components.AccountAssembler
import com.iyanuadelekan.moneytransfer.components.ParameterValidator
import com.iyanuadelekan.moneytransfer.components.ResponseDispatcher
import com.iyanuadelekan.moneytransfer.components.TransactionAssembler
import io.javalin.http.Handler
import com.iyanuadelekan.moneytransfer.services.AccountService
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.TransactionService
import com.iyanuadelekan.moneytransfer.services.TransactionServiceImpl

internal object AccountController {

    private val accountAssembler = AccountAssembler()
    private val transactionAssembler = TransactionAssembler()
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

        ResponseDispatcher.sendSuccess(ctx, accountAssembler.toAccountVO(account), 200)
    }

    internal val listAccounts = Handler { ctx ->
        val (page, limit) = parameterValidator.validateListAccountParams(ctx)
        val accounts = accountService.getAccounts(page, limit)

        ResponseDispatcher.sendSuccess(ctx, accountAssembler.toAccountListVO(accounts), 200)
    }

    internal val getAccountTransactions = Handler { ctx ->
        val (accountId, page, limit) = parameterValidator.validateTransactionRetrievalParams(ctx)
        val transactions = accountService.getAccountTransactions(accountId, page, limit)

        ResponseDispatcher.sendSuccess(ctx, transactionAssembler.toTransactionListVO(transactions), 200)
    }

    internal val fundAccount = Handler { ctx ->
        val (accountId, transactionData) = parameterValidator.validateFundAccountParams(ctx)
        val account = transactionService.processDeposit(accountId, transactionData)

        ResponseDispatcher.sendSuccess(ctx, accountAssembler.toAccountVO(account), 200)
    }

    internal val transferFundsToAccount = Handler { ctx ->
        val (sourceAccountId, recipientAccountId, transactionData) =
                parameterValidator.validateFundTransferParams(ctx)
        val result = transactionService.processTransfer(
                sourceAccountId,
                recipientAccountId,
                transactionData)
        val transferData = transactionAssembler.toTransferVO(result.first, result.second)

        ResponseDispatcher.sendSuccess(ctx, transferData, 200)
    }
}