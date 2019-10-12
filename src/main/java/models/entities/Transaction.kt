package models.entities

import java.math.BigDecimal
import java.util.*

data class Transaction(
        val id: Long,
        val amount: BigDecimal,
        val description: String,
        val balanceBefore: BigDecimal,
        val balanceAfter: BigDecimal,
        val transactionReference: String,
        val sessionReference: String,
        val createdAt: Date,
        val updatedAt: Date
)