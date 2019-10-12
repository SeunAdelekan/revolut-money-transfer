package services

import models.AccountData
import models.entities.Account
import models.entities.Currency
import repositories.*
import java.math.BigDecimal
import java.security.InvalidParameterException

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
    override fun getAccount(accountId: Long): Account {
        return accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")
    }

    @Throws(InvalidParameterException::class)
    override fun fundAccount(accountId: Long, amount: BigDecimal): Account {
        val account = accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")
        println(account.balance)
        account.balance += amount
        accountRepository.save(account)
        return account
    }

    override fun blockAccount(accountId: Long): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unblockAccount(accountId: Long): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transferFunds(senderAccountId: Long, recipientAccountId: Long, amount: BigDecimal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}