package models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import generateUUID
import serializers.DateSerializer
import java.time.Instant
import java.util.*

data class ExchangeRate(
        val sourceCurrency: Currency,
        val targetCurrency: Currency,
        val rate: Double) {
    val id = generateUUID()
    @JsonSerialize(using = DateSerializer::class)
    val createdAt = Date()
    @JsonSerialize(using = DateSerializer::class)
    val updatedAt = Date()
}