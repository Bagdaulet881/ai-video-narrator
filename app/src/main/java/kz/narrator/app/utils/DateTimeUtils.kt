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

fun formatTime(milliseconds: Long): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    val millis = milliseconds % 1000
    return String.format("%02d:%02d:%03d", minutes, seconds, millis)
}

fun convertToMicroseconds(milliseconds: Long): Long {
    return milliseconds * 1000
}