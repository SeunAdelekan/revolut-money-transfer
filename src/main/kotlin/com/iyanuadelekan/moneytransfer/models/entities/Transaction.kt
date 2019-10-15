package com.iyanuadelekan.moneytransfer.models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.constants.TransactionType
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.serializers.BigDecimalSerializer
import com.iyanuadelekan.moneytransfer.serializers.DateSerializer
import java.math.BigDecimal
import java.util.*

data class Transaction(
        @JsonSerialize(using = BigDecimalSerializer::class)
        val amount: BigDecimal,
        // Unique reference for any given transaction
        val transactionReference: String,
        // Session reference tying all debit/credit legs of a transfer
        val sessionReference: String,
        val type: TransactionType,
        val category: TransactionCategory,
        val description: String? = null
) {
        val id: String = generateUUID()

        @JsonSerialize(using = BigDecimalSerializer::class)
        lateinit var balanceBefore: BigDecimal

        @JsonSerialize(using = BigDecimalSerializer::class)
        lateinit var balanceAfter: BigDecimal

        var senderAccountId: String? = null

        var recipientAccountId: String? = null

        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date()

        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date()
}