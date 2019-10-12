package services

import components.Database
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
    override fun getAccount(accountId: String): Account {
        return accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")
    }

    @Throws(InvalidParameterException::class)
    override fun fundAccount(accountId: String, amount: BigDecimal): Account {
        accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")

        val fundedAccount = Database.accountStore.compute(accountId) { _, accountRecord ->
            if (accountRecord == null) {
                accountRecord
            } else {
                accountRecord.balance += amount
                accountRecord
            }
        }
        return fundedAccount as Account
    }

    override fun debitAccount(accountId: String, amount: BigDecimal): Account {
        val account = accountRepository.findById(accountId)
                ?: throw InvalidParameterException("account with id $accountId does not exist")
        println(account.balance)
        if (account.balance < amount) {
            throw exceptions.InvalidParameterException("You do not have enough money to perform this transaction.")
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

    override fun blockAccount(accountId: String): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unblockAccount(accountId: String): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transferFunds(senderAccountId: String, recipientAccountId: String, amount: BigDecimal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}