package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Transaction

interface AccountService {

    fun createBankAccount(accountDetails: AccountData): Account

    fun getAccounts(page: Int = 1, limit: Int = 50): List<Account>

    fun getAccount(accountId: String): Account

    fun getAccountTransactions(accountId: String, page: Int = 1, limit: Int = 50): List<Transaction>

    fun verifyAccountRegistered(accountId: String): Boolean
}