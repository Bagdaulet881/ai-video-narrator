package kz.narrator.app.ui_common.base

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kz.narrator.app.R
import kz.narrator.app.network.exceptions.ApiException
import kz.narrator.app.network.exceptions.ConnectionTimeOutException
import kz.narrator.app.network.exceptions.UnknownException

object ExceptionHandler {

    fun handleException(context: Context, exception: Exception?) {
    }

    fun handleExceptionView(context: Context, exception: Exception?) {
    }

    fun handleExceptionDialog(context: Context, exception: Exception?) {

        fun showDialog(title: String, message: String) {
            AlertDialog
                .Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                }
                .create()
                .show()
        }

        exception?.let {
            when (it) {
                is ApiException -> {
                    println("*****ERROR ${it.apiError.message}")

                    showDialog(
                        title = context.getString(R.string.network_error),
                        message = it.apiError.message ?: ""
                    )
                }
                is ConnectionTimeOutException -> {
                    showDialog(
                        title = context.getString(R.string.network_error),
                        message = context.getString(R.string.network_connection_error)
                    )
                }
                is UnknownException -> {
                    showDialog(
                        title = context.getString(R.string.network_error),
                        message = context.getString(R.string.network_server_error)
                    )
                }
            }
        }

    }

    fun showErrorDialog(context: Context, title: String, message: String) {
        AlertDialog
            .Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
            }
            .create()
            .show()
    }

}