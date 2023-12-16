package kz.narrator.app.data.models.custom

import android.net.Uri

data class Scan(
    var id: String,
    val uri: Uri,
    val extension: String,
    val name: String
){
    fun isImage(): Boolean {
        val extensions =
            mutableListOf("jpg", "jpeg", "png")//, "bmp", "gif", "webp", "heic", "heif")
        return extensions.contains(extension.toLowerCase())
    }

}