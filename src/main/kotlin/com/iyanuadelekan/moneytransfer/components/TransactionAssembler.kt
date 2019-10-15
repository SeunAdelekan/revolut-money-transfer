package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.models.TransactionVO
import com.iyanuadelekan.moneytransfer.models.TransactionOperationVO
import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Transaction

class TransactionAssembler {

    private val accountAssembler = AccountAssembler()

    fun toTransactionOperationVO(account: Account, transaction: Transaction): TransactionOperationVO {
        val transactionOperationVO = TransactionOperationVO()

        transactionOperationVO.account = accountAssembler.toAccountVO(account)
        transactionOperationVO.transaction = toTransactionVO(transaction)

        return transactionOperationVO
    }

    fun toTransactionListVO(transactions: List<Transaction>): List<TransactionVO> {
        return transactions.map(this::toTransactionVO)
    }

    private fun toTransactionVO(transaction: Transaction): TransactionVO {
        val transactionVO = TransactionVO()
        with (transactionVO) {
            id = transaction.id
            amount = transaction.amount
            transactionReference = transaction.transactionReference
            sessionReference = transaction.sessionReference
            amount = transaction.amount
            balanceBefore = transaction.balanceBefore
            balanceAfter = transaction.balanceAfter
            type = transaction.type
            senderAccountId = transaction.senderAccountId
            recipientAccountId = transaction.recipientAccountId
            category = transaction.category
            createdAt = transaction.createdAt
            description = transaction.description
            return this
        }
    }
}