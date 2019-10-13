package models.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import generateUUID
import serializers.DateSerializer
import java.util.*

data class Currency(
        val name: String = "",
        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date(),
        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date()
) {
    val id: String = generateUUID()
}