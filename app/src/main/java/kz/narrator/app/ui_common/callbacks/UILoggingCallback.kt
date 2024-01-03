package kz.narrator.app.ui_common.callbacks

import kz.narrator.app.data.models.ai_data.MappedTimeStamps
import kz.narrator.app.data.models.network.Status

interface UILoggingCallback {

    fun onTimeStampSuccess(sentences: List<MappedTimeStamps>)

    fun onLogState(msg: String, status: Status)

}