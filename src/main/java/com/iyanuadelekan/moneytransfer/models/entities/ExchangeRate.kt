package com.iyanuadelekan.moneytransfer.models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.iyanuadelekan.moneytransfer.generateUUID
import com.iyanuadelekan.moneytransfer.serializers.DateSerializer
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