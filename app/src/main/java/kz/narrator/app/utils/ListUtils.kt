package kz.narrator.app.utils

fun <T> List<T>.splitIntoSublists(maxSublistSize: Int): List<List<T>> {
    val result = ArrayList<List<T>>()
    var start = 0
    while (start < this.size) {
        val end = minOf(start + maxSublistSize, this.size)
        result.add(this.subList(start, end))
        start = end
    }
    return result
}

fun extractSizeFromLog(log: String): String? {
    val regex = "size=\\s*(\\d+\\s*\\w+)".toRegex()
    val matchResult = regex.find(log)
    return matchResult?.groups?.get(1)?.value
}
