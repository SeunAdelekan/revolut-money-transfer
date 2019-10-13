package models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import components.Database
import generateUUID
import serializers.BigDecimalSerializer
import serializers.DateSerializer
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Account(
        val accountName: String = "",
        @JsonSerialize(using = BigDecimalSerializer::class)
        var balance: BigDecimal = BigDecimal(0),
        var status: String = "enabled",
        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date.from(Instant.now()),
        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date.from(Instant.now())
) {
        val id: String = generateUUID()
        lateinit var currency: Currency
        val transactions: MutableList<Transaction> = Collections.synchronizedList(LinkedList<Transaction>())
}