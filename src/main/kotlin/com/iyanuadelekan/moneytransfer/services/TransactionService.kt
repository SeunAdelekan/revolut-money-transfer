package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.TransactionCategory
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Transaction
import java.math.BigDecimal

interface TransactionService {

    fun processDeposit(accountId: String, transactionData: TransactionOperationData): Account

    fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): Pair<Account, Transaction>

    fun withdrawFunds(accountId: String, transactionData: TransactionOperationData): Account

    fun executeDebit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            transactionCategory: TransactionCategory,
            description: String? = null): Pair<Account, Transaction>

    fun executeCredit(
            accountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String? = null): Pair<Account, Transaction>
}