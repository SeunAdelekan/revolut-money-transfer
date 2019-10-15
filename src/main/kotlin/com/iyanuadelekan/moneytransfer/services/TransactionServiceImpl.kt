package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.constants.ErrorMessage
import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.constants.TransactionType
import com.iyanuadelekan.moneytransfer.helpers.InsufficientBalanceException
import com.iyanuadelekan.moneytransfer.helpers.buildAccountIdError
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Transaction
import com.iyanuadelekan.moneytransfer.repositories.TransactionRepository
import com.iyanuadelekan.moneytransfer.repositories.TransactionRepositoryImpl
import java.math.BigDecimal

internal class TransactionServiceImpl : TransactionService {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    @Throws(IllegalArgumentException::class)
    override fun processDeposit(accountId: String, transactionData: TransactionOperationData):
            Pair<Account, Transaction> {
        var account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        require(amount > BigDecimal.ZERO) { ErrorMessage.INVALID_TRANSACTION_AMOUNT.message }
        val convertedAmount = currencyService.getExchangeAmount(amount, currency, account.currency.name)

        val transaction = buildTransaction(
                convertedAmount,
                generateTransactionReference(),
                generateSessionReference(),
                TransactionType.CREDIT,
                TransactionCategory.ACCOUNT_FUNDING)

        account = Datastore.accountStore.compute(accountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance += convertedAmount
                transaction.balanceBefore = accountRecord.balance - convertedAmount
                transaction.balanceAfter = accountRecord.balance
                accountRecord.transactions.add(0, transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException(buildAccountIdError(accountId))

        return Pair(account, transaction)
    }

    override fun withdrawFunds(accountId: String, transactionData: TransactionOperationData):
            Pair<Account, Transaction> {
        val account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        require(amount > BigDecimal.ZERO) { ErrorMessage.INVALID_TRANSACTION_AMOUNT.message }
        val convertedAmount = currencyService.getExchangeAmount(amount, currency, account.currency.name)

        return executeDebit(
                accountId,
                convertedAmount, generateSessionReference(),
                TransactionCategory.ACCOUNT_WITHDRAWAL)
    }

    override fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): Pair<Account, Transaction> {
        val (amount, currency, description) = transactionData

        require(amount > BigDecimal.ZERO) { ErrorMessage.INVALID_TRANSACTION_AMOUNT.message }

        val sourceAccount = accountService.getAccount(sourceAccountId)
        val sourceDebitAmount = currencyService.getExchangeAmount(amount, currency, sourceAccount.currency.name)

        if (sourceAccount.balance < sourceDebitAmount) {
            throw InsufficientBalanceException(sourceDebitAmount)
        }
        val targetAccount = accountService.getAccount(recipientAccountId)
        val destinationCreditAmount = currencyService.getExchangeAmount(amount, currency, targetAccount.currency.name)
        val sessionReference = generateSessionReference()
        val debitResult = executeDebit(
                sourceAccountId,
                sourceDebitAmount,
                sessionReference,
                TransactionCategory.BANK_TRANSFER,
                description,
                recipientAccountId)
        executeCredit(sourceAccountId, recipientAccountId, destinationCreditAmount, sessionReference, description)

        return debitResult
    }

    @Throws(IllegalArgumentException::class)
    override fun executeDebit(
            sourceAccountId: String,
            amount: BigDecimal,
            sessionReference: String,
            transactionCategory: TransactionCategory,
            description: String?,
            recipientAccountId: String?): Pair<Account, Transaction> {
        val transactionReference = generateTransactionReference()
        val transaction = buildTransaction(
                amount,
                transactionReference,
                sessionReference,
                TransactionType.DEBIT,
                transactionCategory,
                description,
                sourceAccountId,
                recipientAccountId)

        val debitedAccount = Datastore.accountStore.compute(sourceAccountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                if (accountRecord.balance < amount) {
                    throw InsufficientBalanceException(amount)
                }
                accountRecord.balance -= amount
                transaction.balanceBefore = accountRecord.balance + amount
                transaction.balanceAfter = accountRecord.balance
                accountRecord.transactions.add(0, transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException(buildAccountIdError(sourceAccountId))

        return Pair(debitedAccount, transaction)
    }

    @Throws(IllegalArgumentException::class)
    override fun executeCredit(
            sourceAccountId: String,
            recipientAccountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String?): Pair<Account, Transaction> {
        val transactionReference = generateTransactionReference()
        val transaction = buildTransaction(
                amount,
                transactionReference,
                sessionReference,
                TransactionType.CREDIT,
                TransactionCategory.BANK_TRANSFER,
                description,
                sourceAccountId,
                recipientAccountId)

        val creditedAccount = Datastore.accountStore.compute(recipientAccountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance += amount
                transaction.balanceBefore = accountRecord.balance - amount
                transaction.balanceAfter = accountRecord.balance
                accountRecord.transactions.add(0, transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException(buildAccountIdError(recipientAccountId))

        return Pair(creditedAccount, transaction)
    }

    private fun buildTransaction(
            amount: BigDecimal,
            transactionReference: String,
            sessionReference: String,
            type: TransactionType,
            category: TransactionCategory,
            description: String? = null,
            senderAccountId: String? = null,
            recipientAccountId: String? = null): Transaction {
        val transaction = Transaction(amount, transactionReference, sessionReference, type, category, description)

        if (category == TransactionCategory.BANK_TRANSFER && type == TransactionType.DEBIT) {
            transaction.recipientAccountId = recipientAccountId
        }
        if (category == TransactionCategory.BANK_TRANSFER && type == TransactionType.CREDIT) {
            transaction.senderAccountId = senderAccountId
        }
        return transaction
    }

    private fun generateSessionReference() = "SESSION-${generateUUID()}}"

    private fun generateTransactionReference() = "TXN-${generateUUID()}"
}