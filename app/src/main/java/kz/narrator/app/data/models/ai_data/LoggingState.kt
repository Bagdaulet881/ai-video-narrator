package kz.narrator.app.data.models.ai_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.narrator.app.R
import kz.narrator.app.data.models.network.Status

data class LoggingState(
    val id: Int,
    val msg: String,
    val status: Status
) {
    fun getTextColor(): Int {
        return when (this.status) {
            Status.SUCCESS -> {
                R.color.statusSuccess
            }
            Status.ERROR -> {
                R.color.statusError
            }
            else -> {
                R.color.white
            }
        }
    }

    fun getStatusMsg(): String {
        return when (this.status) {
            Status.SUCCESS -> {
                "Success."
            }
            Status.ERROR -> {
                "Error!"
            }
            else -> {
                "Loading.."
            }
        }
    }
}