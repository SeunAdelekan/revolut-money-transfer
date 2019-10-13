package models.entities

import TransactionCategory
import TransactionType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import generateUUID
import serializers.BigDecimalSerializer
import serializers.DateSerializer
import java.math.BigDecimal
import java.util.*

data class Transaction(
        @JsonSerialize(using = BigDecimalSerializer::class)
        val amount: BigDecimal,
        val transactionReference: String,
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

        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date()

        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date()
}