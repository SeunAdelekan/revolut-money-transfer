package services

import models.TransactionOperationData
import models.entities.Account
import models.entities.Transaction

interface TransactionService {

    fun processDeposit(accountId: Long, transactionData: TransactionOperationData): Account

    fun executeDebit(account: Account, transactionData: TransactionOperationData): Transaction

    fun executeCredit(account: Account, transactionData: TransactionOperationData): Transaction

    fun getTransactions(accountId: Long, page: Long = 1, limit: Long = 50)
}