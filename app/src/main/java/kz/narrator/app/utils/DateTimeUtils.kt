package kz.narrator.app.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun convertStringToDate(dateInString: String, pattern: String): Date? {
    val sdfFrom = SimpleDateFormat(pattern)
    var date: Date? = null

    try {
        date = sdfFrom.parse(dateInString)

    } catch (e: ParseException) {
//        e.printStackTrace()
    }

    return date
}

fun convertDateToString(date: Date, pattern: String): String {
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(date)
}