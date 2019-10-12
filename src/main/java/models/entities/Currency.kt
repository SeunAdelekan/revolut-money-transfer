package models.entities

import java.util.*

data class Currency(
        val id: Long = 0,
        val name: String = "",
        val createdAt: Date = Date(),
        val updatedAt: Date = Date()
) {
}