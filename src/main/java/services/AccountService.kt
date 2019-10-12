package services

import models.AccountData
import models.entities.Account
import java.math.BigDecimal

interface AccountService {

    fun createBankAccount(accountDetails: AccountData): Account

    fun getAccounts(page: Int = 1, limit: Int = 50): List<Account>

    fun getAccount(accountId: String): Account

    fun blockAccount(accountId: String): Account

    fun unblockAccount(accountId: String): Account

    fun fundAccount(accountId: String, amount: BigDecimal): Account

    fun debitAccount(accountId: String, amount: BigDecimal): Account

    fun transferFunds(senderAccountId: String, recipientAccountId: String, amount: BigDecimal)
}