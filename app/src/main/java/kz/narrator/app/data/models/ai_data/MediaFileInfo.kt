package kz.narrator.app.data.models.ai_data

import android.net.Uri

data class MediaFileInfo(
    val mediaFilePath: Uri,
    val frameBestShot: Long? = null
)