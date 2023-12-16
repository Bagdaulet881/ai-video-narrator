package kz.narrator.app.utils

import android.app.Activity
import android.util.DisplayMetrics

fun getDisplayMetrics(activity: Activity?): DisplayMetrics {
    val display = activity!!.windowManager.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)

    return metrics
}