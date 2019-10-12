package models.entities

import java.time.Instant
import java.util.*

class ExchangeRate {
    var id: Long = 0
    lateinit var sourceCurrency: Currency
    lateinit var targetCurrency: Currency
    val createdAt: Date = Date.from(Instant.now())
    val updatedAt: Date = Date.from(Instant.now())
}