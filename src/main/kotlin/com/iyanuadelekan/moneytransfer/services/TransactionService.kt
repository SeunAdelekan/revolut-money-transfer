package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Transaction
import java.math.BigDecimal

interface TransactionService {

    fun processDeposit(accountId: String, transactionData: TransactionOperationData): Pair<Account, Transaction>

    fun processTransfer(
            sourceAccountId: String,
            recipientAccountId: String,
            transactionData: TransactionOperationData): Pair<Account, Transaction>

    fun withdrawFunds(accountId: String, transactionData: TransactionOperationData): Pair<Account, Transaction>

    fun executeDebit(
            sourceAccountId: String,
            amount: BigDecimal,
            sessionReference: String,
            transactionCategory: TransactionCategory,
            description: String? = null,
            recipientAccountId: String? = null): Pair<Account, Transaction>

    fun executeCredit(
            sourceAccountId: String,
            recipientAccountId: String,
            amount: BigDecimal,
            sessionReference: String,
            description: String? = null): Pair<Account, Transaction>
}