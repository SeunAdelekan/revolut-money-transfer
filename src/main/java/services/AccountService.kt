package services

import models.AccountData
import models.entities.Account
import java.math.BigDecimal

interface AccountService {

    fun createBankAccount(accountDetails: AccountData): Account

    fun getAccounts(page: Int = 1, limit: Int = 50): List<Account>

    fun getAccount(accountId: Long): Account

    fun blockAccount(accountId: Long): Account

    fun unblockAccount(accountId: Long): Account

    fun fundAccount(accountId: Long, amount: BigDecimal): Account

    fun transferFunds(senderAccountId: Long, recipientAccountId: Long, amount: BigDecimal)
}