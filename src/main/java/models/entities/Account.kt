package models.entities

import components.Database
import generateUUID
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Account(
        val accountName: String = "",
        var balance: BigDecimal = BigDecimal(0),
        var status: String = "enabled",
        val createdAt: Date = Date.from(Instant.now()),
        val updatedAt: Date = Date.from(Instant.now())
) {
        val id: String = generateUUID()
        lateinit var currency: Currency
        val transactions: MutableList<Transaction> = Collections.synchronizedList(LinkedList<Transaction>())
}