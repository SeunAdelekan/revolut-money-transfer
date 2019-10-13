import java.util.*
import kotlin.math.min


fun generateUUID() = UUID.randomUUID().toString().replace("-", "")

fun <T> List<T>.getPage(page: Int, limit: Int): List<T> {
    require(!(limit <= 0 || page <= 0)) { "invalid attributes of page: $page, limit: $limit" }

    val start = (page - 1) * limit

    return if (this.size < start) {
        emptyList()
    } else this.subList(start, min(start + limit, this.size))
}