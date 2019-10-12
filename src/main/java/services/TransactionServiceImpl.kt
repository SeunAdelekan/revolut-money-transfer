package services

import components.Database
import exceptions.InvalidParameterException
import generateUUID
import models.TransactionOperationData
import models.TransferVO
import models.entities.Account
import models.entities.Transaction
import repositories.TransactionRepository
import repositories.TransactionRepositoryImpl
import java.math.BigDecimal
import java.util.*

internal class TransactionServiceImpl : TransactionService, BaseServiceImpl() {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    override fun processDeposit(accountId: String, transactionData: TransactionOperationData): Account {
        var account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        if (amount <= BigDecimal(0)) {
            throw InvalidParameterException("Transaction amounts must be greater than 0.0")
        }
        val convertedAmount = currencyService.getExchangeAmount(amount, currency, account.currency.name)
        account = accountService.fundAccount(account.id, convertedAmount)
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
        val debitedAccount = accountService.debitAccount(accountId, amount)
        val transaction = Transaction(
                amount = amount,
                balanceBefore = debitedAccount.balance  + amount,
                balanceAfter = debitedAccount.balance,
                description = description,
                sessionReference = sessionReference,
                transactionReference = transactionReference)
        return Pair(debitedAccount, transaction)
    }

    @Throws(InvalidParameterException::class)
    override fun executeCredit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String?): Pair<Account, Transaction> {
        val transactionReference = generateTransactionReference()

        val creditedAccount = accountService.fundAccount(accountId, amount)
        val transaction = Transaction(
                amount = amount,
                balanceBefore = creditedAccount.balance  + amount,
                balanceAfter = creditedAccount.balance,
                description = description,
                sessionReference = sessionReference,
                transactionReference = transactionReference)
        return Pair(creditedAccount, transaction)
    }

    override fun getTransactions(accountId: String, page: Long, limit: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun generateSessionReference() = "SESSION-${generateUUID()}}"

    private fun generateTransactionReference() = "TXN-${generateUUID()}"
}