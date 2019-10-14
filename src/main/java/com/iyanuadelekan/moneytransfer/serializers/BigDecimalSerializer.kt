package com.iyanuadelekan.moneytransfer.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * @author Iyanu Adelekan on 09/02/2019.
 */
class BigDecimalSerializer : JsonSerializer<BigDecimal>() {

    override fun serialize(decimal: BigDecimal, generator: JsonGenerator, provider: SerializerProvider) {
        val serializedDecimal = DecimalFormat("#.##").apply {
            minimumFractionDigits = 2
        }.format(decimal.setScale(2, RoundingMode.HALF_UP))

        generator.writeString(serializedDecimal)
    }
}