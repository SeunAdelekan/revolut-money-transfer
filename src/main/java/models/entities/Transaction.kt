package models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import generateUUID
import serializers.BigDecimalSerializer
import serializers.DateSerializer
import java.math.BigDecimal
import java.util.*

data class Transaction(
        @JsonSerialize(using = BigDecimalSerializer::class)
        val amount: BigDecimal,
        val balanceBefore: BigDecimal,
        val balanceAfter: BigDecimal,
        val transactionReference: String,
        val sessionReference: String,
        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date(),
        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date(),
        val description: String? = null
) {
    val id: String = generateUUID()
}