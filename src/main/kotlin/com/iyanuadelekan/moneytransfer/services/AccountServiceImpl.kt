package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.getPage
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Currency
import com.iyanuadelekan.moneytransfer.models.entities.Transaction
import com.iyanuadelekan.moneytransfer.repositories.AccountRepository
import com.iyanuadelekan.moneytransfer.repositories.AccountRepositoryImpl
import com.iyanuadelekan.moneytransfer.repositories.CurrencyRepository
import com.iyanuadelekan.moneytransfer.repositories.CurrencyRepositoryImpl
import java.lang.IllegalArgumentException

class AccountServiceImpl : AccountService {

    private val accountRepository: AccountRepository = AccountRepositoryImpl()
    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl()

    override fun createBankAccount(accountDetails: AccountData): Account {
        val currency = currencyRepository.findByName(accountDetails.currency) as Currency
        val account = Account(accountDetails.accountName)
        account.currency = currency
        return accountRepository.save(account)
    }

    override fun getAccounts(page: Int, limit: Int): List<Account> = accountRepository.findByPage(page, limit)

    @Throws(IllegalArgumentException::class)
    override fun getAccount(accountId: String): Account {
        return accountRepository.findById(accountId)
                ?: throw IllegalArgumentException("account with id $accountId does not exist")
    }

    override fun getAccountTransactions(accountId: String, page: Int, limit: Int): List<Transaction>
            = getAccount(accountId).transactions.getPage(page, limit)

    override fun verifyAccountRegistered(accountId: String): Boolean = accountRepository.findById(accountId) != null
}