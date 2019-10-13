package services

import InvalidParameterException
import components.Database
import getPage
import models.AccountData
import models.entities.Account
import models.entities.Currency
import models.entities.Transaction
import repositories.*
import java.math.BigDecimal

class AccountServiceImpl : AccountService, BaseServiceImpl() {

    private val accountRepository: AccountRepository = AccountRepositoryImpl()
    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl()

    override fun createBankAccount(accountDetails: AccountData): Account {
        val currency = currencyRepository.findByName(accountDetails.currency) as Currency
        val account = Account(accountName = accountDetails.accountName)
        account.currency = currency
        return accountRepository.save(account)
    }

    override fun getAccounts(page: Int, limit: Int): List<Account> = accountRepository.findByPage(page, limit)

    @Throws(InvalidParameterException::class)
    override fun getAccount(accountId: String): Account {
        return accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")
    }

    @Throws(IllegalArgumentException::class)
    override fun fundAccount(accountId: String, amount: BigDecimal): Account {
        return Database.accountStore.compute(accountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance += amount
                accountRecord
            }
        } ?: throw IllegalArgumentException("account with id $accountId does not exist")
    }

    override fun debitAccount(accountId: String, amount: BigDecimal): Account {
        val account = getAccount(accountId)

        if (account.balance < amount) {
            throw InvalidParameterException("You do not have enough money to perform this transaction.")
        }
        val debitedAccount = Database.accountStore.compute(accountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance -= amount
                accountRecord
            }
        }
        return debitedAccount as Account
    }

    override fun transferFunds(senderAccountId: String, recipientAccountId: String, amount: BigDecimal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccountTransactions(accountId: String, page: Int, limit: Int): List<Transaction>
            = getAccount(accountId).transactions.getPage(page, limit)
}