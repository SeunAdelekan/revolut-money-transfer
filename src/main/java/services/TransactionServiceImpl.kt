package services

import InsufficientBalanceException
import InvalidParameterException
import TransactionCategory
import TransactionType
import components.Datastore
import generateUUID
import models.TransactionOperationData
import models.TransferVO
import models.entities.Account
import models.entities.Transaction
import repositories.TransactionRepository
import repositories.TransactionRepositoryImpl
import java.math.BigDecimal

internal class TransactionServiceImpl : TransactionService, BaseServiceImpl() {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    override fun processDeposit(accountId: String, transactionData: TransactionOperationData): Account {
        var account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        if (amount <= BigDecimal.ZERO) {
            throw InvalidParameterException("Transaction amounts must be greater than 0.0")
        }
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
                accountRecord.transactions.add(transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")

        return account
    }

    override fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): TransferVO {
        val (amount, currency, description) = transactionData

        if (amount <= BigDecimal.ZERO) {
            throw InvalidParameterException("Transaction amounts must be greater than 0.0")
        }
        val sourceAccount = accountService.getAccount(sourceAccountId)
        val sourceDebitAmount = currencyService.getExchangeAmount(amount, currency, sourceAccount.currency.name)

        if (sourceAccount.balance < sourceDebitAmount) {
            throw InvalidParameterException("You do not have enough money to perform this transaction.")
        }
        val targetAccount = accountService.getAccount(recipientAccountId)
        val destinationCreditAmount = currencyService.getExchangeAmount(amount, currency, targetAccount.currency.name)
        val sessionReference = generateSessionReference()
        val debitResult = executeDebit(sourceAccountId, sourceDebitAmount, sessionReference, description)
        executeCredit(recipientAccountId, destinationCreditAmount, sessionReference, description)

        return TransferVO(debitResult.first, debitResult.second)
    }

    override fun executeDebit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
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
                accountRecord.transactions.add(transactionRepository.save(transaction))
                accountRecord
            }
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")

        return Pair(debitedAccount, transaction)
    }

    @Throws(InvalidParameterException::class)
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
                accountRecord.transactions.add(transactionRepository.save(transaction))
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