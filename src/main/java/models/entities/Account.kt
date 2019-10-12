package models.entities

import components.Database
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Account(
        val id: Long = (Database.accountStore.size + 1).toLong(),
        val accountName: String = "",
        var balance: BigDecimal = BigDecimal(0),
        var status: String = "enabled",
        val createdAt: Date = Date.from(Instant.now()),
        val updatedAt: Date = Date.from(Instant.now())
) {
//        @OneToMany(cascade = [CascadeType.ALL])
//        @JoinColumn(name = "sender_account_id")
//        val debitTransactions: Set<Transaction> = HashSet()
//
//        @OneToMany(cascade = [CascadeType.ALL])
//        @JoinColumn(name = "recipient_account_id")
//        val creditTransactions: Set<Transaction> = HashSet()
        lateinit var currency: Currency
}