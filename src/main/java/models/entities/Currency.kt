package models.entities

import generateUUID
import java.util.*

data class Currency(
        val name: String = "",
        val createdAt: Date = Date(),
        val updatedAt: Date = Date()
) {
    val id: String = generateUUID()
}