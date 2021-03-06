package com.iyanuadelekan.moneytransfer.models.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.serializers.BigDecimalSerializer
import com.iyanuadelekan.moneytransfer.serializers.DateSerializer
import java.math.BigDecimal
import java.util.*

data class Account(
        val accountName: String,
        @JsonSerialize(using = BigDecimalSerializer::class)
        var balance: BigDecimal = BigDecimal(0),
        var status: String = "enabled",
        @JsonIgnore
        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date(),
        @JsonIgnore
        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date()
) {
        val id: String = generateUUID()

        lateinit var currency: Currency

        @JsonIgnore
        val transactions: MutableList<Transaction> = Collections.synchronizedList(LinkedList<Transaction>())
}