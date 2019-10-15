package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.*
import com.iyanuadelekan.moneytransfer.components.Datastore
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
    override fun processDeposit(accountId: String, transactionData: TransactionOperationData): Account {
        var account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        require(amount > BigDecimal.ZERO) { "Transaction amounts must be greater than 0.00" }
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
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")

        return account
    }

    override fun withdrawFunds(accountId: String, transactionData: TransactionOperationData): Account {
        val account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        require(amount > BigDecimal.ZERO) { "Transaction amounts must be greater than 0.00" }
        val convertedAmount = currencyService.getExchangeAmount(amount, currency, account.currency.name)

        val debitResult = executeDebit(
                accountId,
                convertedAmount,generateSessionReference(),
                TransactionCategory.ACCOUNT_WITHDRAWAL)

        return debitResult.first
    }

    override fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): Pair<Account, Transaction> {
        val (amount, currency, description) = transactionData

        require(amount > BigDecimal.ZERO) { "Transaction amounts must be greater than 0.00" }

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
                description)
        executeCredit(recipientAccountId, destinationCreditAmount, sessionReference, description)

        return debitResult
    }

    @Throws(IllegalArgumentException::class)
    override fun executeDebit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            transactionCategory: TransactionCategory,
            description: String?): Pair<Account, Transaction> {
        val transactionReference = generateTransactionReference()
        val transaction = buildTransaction(
                amount,
                transactionReference,
                sessionReference,
                TransactionType.DEBIT,
                TransactionCategory.BANK_TRANSFER,
                description)

        val debitedAccount = Datastore.accountStore.compute(accountId) { _, accountRecord ->
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
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")

        return Pair(debitedAccount, transaction)
    }

    @Throws(IllegalArgumentException::class)
    override fun executeCredit(
            accountId: String,
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
                description)

        val creditedAccount = Datastore.accountStore.compute(accountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance += amount
                transaction.balanceBefore = accountRecord.balance - amount
                transaction.balanceAfter = accountRecord.balance
                accountRecord.transactions.add(0, transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")

        return Pair(creditedAccount, transaction)
    }

    private fun buildTransaction(
            amount: BigDecimal,
            transactionReference: String,
            sessionReference: String,
            type: TransactionType,
            category: TransactionCategory,
            description: String? = null): Transaction
            = Transaction(amount, transactionReference, sessionReference, type, category, description)

    private fun generateSessionReference() = "SESSION-${generateUUID()}}"

    private fun generateTransactionReference() = "TXN-${generateUUID()}"
}