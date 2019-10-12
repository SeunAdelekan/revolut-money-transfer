package services

import models.TransactionOperationData
import models.TransferVO
import models.entities.Account
import models.entities.Transaction
import java.math.BigDecimal

interface TransactionService {

    fun processDeposit(accountId: String, transactionData: TransactionOperationData): Account

    fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): TransferVO

    fun executeDebit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String? = null): Pair<Account, Transaction>

    fun executeCredit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String? = null): Pair<Account, Transaction>

    fun getTransactions(accountId: String, page: Long = 1, limit: Long = 50)
}