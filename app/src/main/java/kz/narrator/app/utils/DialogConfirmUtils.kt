package kz.narrator.app.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

fun showConfirmDialog(
    context: Context,
    messageRes: Int,
    deleteConfirmBtnClickListener: () -> Unit,
    @StringRes positiveBtnText: Int,
    @StringRes negativeBtnText: Int
) {
    AlertDialog.Builder(context)
        .setMessage(messageRes)
        .setPositiveButton(
            positiveBtnText
        ) { dialog, which -> deleteConfirmBtnClickListener.invoke() }
        .setNegativeButton(
            negativeBtnText
        ) { dialog, which -> dialog.cancel() }
        .create()
        .show()
}