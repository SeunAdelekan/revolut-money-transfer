package com.iyanuadelekan.moneytransfer.models.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.serializers.DateSerializer
import java.util.*

data class Currency(
        val name: String,
        @JsonIgnore
        @JsonSerialize(using = DateSerializer::class)
        val createdAt: Date = Date(),
        @JsonIgnore
        @JsonSerialize(using = DateSerializer::class)
        val updatedAt: Date = Date()
) {
    val id: String = generateUUID()
}