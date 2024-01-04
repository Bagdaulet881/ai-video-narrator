package kz.narrator.app.ui_common.callbacks

import kz.narrator.app.data.models.network.Status
import java.io.File

interface FFMpegCallback {

    fun onProgress(progress: String)

    fun onSuccess(msg: String)

    fun onFailure(error: Exception)

    fun onNotAvailable(error: Exception)

    fun onFinish()

    fun onLogStatus(msg: String, status: Status)

}