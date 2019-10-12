package services

import exceptions.InvalidParameterException
import models.TransactionOperationData
import models.entities.Account
import models.entities.Transaction
import repositories.TransactionRepository
import repositories.TransactionRepositoryImpl
import java.math.BigDecimal

class TransactionServiceImpl : TransactionService, BaseServiceImpl() {

    private val accountService: AccountService = AccountServiceImpl()
    private val currencyService: CurrencyService = CurrencyServiceImpl()
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    override fun processDeposit(accountId: Long, transactionData: TransactionOperationData): Account {
        val account = accountService.getAccount(accountId)
        val (amount, currency) = transactionData

        if (amount <= BigDecimal(0)) {
            throw InvalidParameterException("Transaction amounts must be greater than 0.0")
        }
        val convertedAmount = currencyService.getExchangeAmount(amount, currency, account.currency.name)
        return accountService.fundAccount(account.id, convertedAmount)

//        val creditedAccount = accountService.fundAccount(
//                    account.id,
//                    convertedAmount
//            )
    }

    override fun executeDebit(account: Account, transactionData: TransactionOperationData): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Throws(InvalidParameterException::class)
    override fun executeCredit(account: Account, transactionData: TransactionOperationData): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTransactions(accountId: Long, page: Long, limit: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}