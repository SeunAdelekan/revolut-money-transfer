package services

import models.AccountData
import models.entities.Account
import models.entities.Transaction
import java.math.BigDecimal

interface AccountService {

    fun createBankAccount(accountDetails: AccountData): Account

    fun getAccounts(page: Int = 1, limit: Int = 50): List<Account>

    fun getAccount(accountId: String): Account

    fun getAccountTransactions(accountId: String, page: Int = 1, limit: Int = 50): List<Transaction>
}