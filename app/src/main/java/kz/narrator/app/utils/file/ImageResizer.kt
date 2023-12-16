package kz.narrator.app.utils.file

import android.graphics.Bitmap
import kotlin.math.roundToInt
import kotlin.math.sqrt


class ImageResizer {

    fun reduceBitmapSize(bitmap: Bitmap, MAX_SIZE: Int): Bitmap? {
        val ratioSquare: Double
        val bitmapHeight: Int = bitmap.height
        val bitmapWidth: Int = bitmap.width
        ratioSquare = bitmapHeight * bitmapWidth / MAX_SIZE.toDouble()
        if (ratioSquare <= 1) return bitmap
        val ratio = sqrt(ratioSquare)
        val requiredHeight = (bitmapHeight / ratio).roundToInt().toInt()
        val requiredWidth = (bitmapWidth / ratio).roundToInt().toInt()
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)
    }

}