package kz.narrator.app.ui_common.callbacks

import android.graphics.Bitmap
import kz.narrator.app.data.models.ai_data.MediaFileInfo

interface MainMediaCallback {

    fun onImageCropped(frames: List<Bitmap>)
    fun getSelectedMediaInfo(): List<MediaFileInfo>

    fun onImageAnalyzed(analyzedText: String)
    fun getCroppedImages(): List<Bitmap>

    fun onTextToVoice(audioFileName: String) //audio file name
    fun getAnalyzedText(): String
}