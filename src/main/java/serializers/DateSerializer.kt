package serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DateSerializer : JsonSerializer<Date>() {

    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

    @Throws(IOException::class)
    override fun serialize(date: Date, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(dateFormatter.format(date))
    }
}