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