package models.entities

import generateUUID
import java.math.BigDecimal
import java.util.*

data class Transaction(
        val amount: BigDecimal,
        val balanceBefore: BigDecimal,
        val balanceAfter: BigDecimal,
        val transactionReference: String,
        val sessionReference: String,
        val createdAt: Date = Date(),
        val updatedAt: Date = Date(),
        val description: String? = null
) {
    val id: String = generateUUID()
}