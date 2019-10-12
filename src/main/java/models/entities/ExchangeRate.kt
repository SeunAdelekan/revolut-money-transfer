package models.entities

import generateUUID
import java.time.Instant
import java.util.*

data class ExchangeRate(
        val sourceCurrency: Currency,
        val targetCurrency: Currency,
        val rate: Double) {
    val id = generateUUID()
    val createdAt = Date()
    val updatedAt = Date()
}