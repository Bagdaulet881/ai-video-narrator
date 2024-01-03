package kz.narrator.app.ui.main_page

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.narrator.app.data.models.ai_data.LoggingState
import kz.narrator.app.data.models.ai_data.MappedTimeStamps
import kz.narrator.app.data.models.ai_data.MediaFileInfo
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import kz.narrator.app.ui_common.callbacks.FFMpegCallback
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import kz.narrator.app.utils.convertToMicroseconds
import kz.narrator.app.utils.formatTime
import kz.narrator.app.utils.live_data.Event
import java.io.File
import java.util.*
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {

    private val _listOfLogs = MutableLiveData<List<Any>>()
    val listOfLogs: LiveData<List<Any>>
        get() = _listOfLogs

    private val _playAudio = MutableLiveData<Event<String>>()
    val playAudio: LiveData<Event<String>>
        get() = _playAudio

    private val _runBottomSheet = MutableLiveData<Event<Unit>>()
    val runBottomSheet: LiveData<Event<Unit>>
        get() = _runBottomSheet

    private val _runVideoPlayer = MutableLiveData<Event<Uri>>()
    val runVideoPlayer: LiveData<Event<Uri>>
        get() = _runVideoPlayer

    private val _runVideoCrop = MutableLiveData<Event<Unit>>()
    val runVideoCrop: LiveData<Event<Unit>>
        get() = _runVideoCrop

    private val _runOpenAi = MutableLiveData<Event<Unit>>()
    val runOpenAi: LiveData<Event<Unit>>
        get() = _runOpenAi

    private val _runVoiceLab = MutableLiveData<Event<Unit>>()
    val runVoiceLab: LiveData<Event<Unit>>
        get() = _runVoiceLab

    private val _runResultScreen = MutableLiveData<Event<Unit>>()
    val runResultScreen: LiveData<Event<Unit>>
        get() = _runResultScreen

    private val _createVideo = MutableLiveData<Event<Unit>>()
    val createVideo: LiveData<Event<Unit>>
        get() = _createVideo

    val isMediaEditVisible = MutableLiveData<Boolean>(false)
    val currentFrameMsFieldText = MutableLiveData<String>()

    //Api DATA
    val bestshotMinisec = MutableLiveData<Long>()
    val attachedMedia = MutableLiveData<Scan>()

    val croppedImagesData = MutableLiveData<List<Bitmap>>()
    val analyzedTextData = MutableLiveData<String>()
    val audioFileNameData = MutableLiveData<String>()

    /**
     *
     */
    val uiLoggingCallback = object : UILoggingCallback {
        override fun onTimeStampSuccess(sentences: List<MappedTimeStamps>) {
            Log.i("MSG", "MAIN_VM_UI_LOG: onTimeStampSuccess ${sentences}")
        }

        override fun onLogState(msg: String, status: Status) {
            val sz = _listOfLogs.value?.lastIndex
            val cnt = if(sz == null) 0 else (_listOfLogs.value?.get(sz) as LoggingState).id + 1

            setLogList(
                LoggingState(
                    id = cnt,
                    msg = msg,
                    status = status
                )
            )
            Log.i("MSG", "MAIN_VM_UI_LOG: ${msg}")
        }

    }

    fun setLogList(item: LoggingState) {
        val temp = mutableListOf<Any>()

        _listOfLogs.value?.let {
            temp.addAll(it)
        }
        temp.add(item)

        _listOfLogs.postValue(temp)
    }

    /**
     *
     */
    val mainAnalyzeCallback = object : MainMediaCallback {
        override fun onImageCropped(frames: List<Bitmap>) {
            Log.i("MSG", "BIG_LOGGER: onImageCropped ${frames.size}")
            uiLoggingCallback.onLogState("MAIN: frame cropped", Status.SUCCESS)
            croppedImagesData.postValue(frames)
            _runOpenAi.postValue(Event(Unit))
        }

        override fun getSelectedMediaInfo(): List<MediaFileInfo> {
            if (attachFilesCallback.getAttachedFiles().isNotEmpty()) {
                Log.i("MSG", "BIG_LOGGER: getSelectedMediaInfo OK")
                uiLoggingCallback.onLogState("MAIN: getSelectedMediaInfo", Status.SUCCESS)
                return listOf(
                    MediaFileInfo(
                        mediaFilePath = attachedMedia.value!!.uri,
                        frameBestShot = bestshotMinisec.value
                    )
                )
            } else {
                Log.i("MSG", "BIG_LOGGER: getSelectedMediaInfo error")
                uiLoggingCallback.onLogState("MAIN: getSelectedMediaInfo", Status.ERROR)
                //TODO 881 Log error
                return emptyList()
            }
        }

        override fun onImageAnalyzed(analyzedText: String) {
            Log.i("MSG", "BIG_LOGGER: onImageAnalyzed ${analyzedText}")
            uiLoggingCallback.onLogState("MAIN: onImageAnalyzed", Status.SUCCESS)
            analyzedTextData.postValue(analyzedText)
            _runVoiceLab.postValue(Event(Unit))
        }

        override fun getCroppedImages(): List<Bitmap> {
            if (croppedImagesData.value?.isNotEmpty() == true) {
                Log.i("MSG", "BIG_LOGGER: getCroppedImages OK")
                uiLoggingCallback.onLogState("MAIN: getCroppedImages", Status.SUCCESS)
                return croppedImagesData.value!!
            } else {
                uiLoggingCallback.onLogState("MAIN: getCroppedImages", Status.ERROR)
                Log.i("MSG", "BIG_LOGGER: getCroppedImages error")
                //TODO 881 Log error
                return emptyList()
            }
        }

        override fun onTextToVoice(audioFileName: String) {
            audioFileNameData.postValue(audioFileName)
            uiLoggingCallback.onLogState("MAIN: onTextToVoice", Status.SUCCESS)
            Log.i("MSG", "BIG_LOGGER: onTextToVoice ${audioFileName}")
        }

        override fun getAnalyzedText(): String {
            if (analyzedTextData.value?.isNotEmpty() == true) {
                uiLoggingCallback.onLogState("MAIN: getAnalyzedText", Status.SUCCESS)
                Log.i("MSG", "BIG_LOGGER: getAnalyzedText OK")
                return analyzedTextData.value!!
            } else {
                uiLoggingCallback.onLogState("MAIN: getAnalyzedText", Status.ERROR)
                Log.i("MSG", "BIG_LOGGER: getAnalyzedText error")
                //TODO 881 Log error
                return ""
            }
        }

    }

    /**
     *
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    fun setMediaAttachedStatus(isAttached: Boolean) {
        val media = attachFilesCallback.getAttachedFiles().get(0) as Scan
        attachedMedia.postValue(media)
        Log.i("MSG", "FFMPEG_LOG: media attached")
        _runVideoPlayer.postValue(Event(media.uri))
        isMediaEditVisible.postValue(isAttached)
    }

    /**
     *
     */
    val ffMpegCallback = object : FFMpegCallback {
        override fun onProgress(progress: String) {
            Log.i("MSG", "FFMPEG_LOG: onProgress ${progress}")
        }

        override fun onSuccess(convertedFile: File, type: String) {
            uiLoggingCallback.onLogState(
                "FFMPEG: file converted ${convertedFile.path}",
                Status.SUCCESS
            )
            Log.i("MSG", "FFMPEG_LOG: onSuccess ${type} ${convertedFile.path}")
        }

        override fun onFailure(error: Exception) {
            uiLoggingCallback.onLogState("FFMPEG: onFailure ${error.cause}", Status.ERROR)
            Log.i("MSG", "FFMPEG_LOG: onFailure ${error.cause}")
        }

        override fun onNotAvailable(error: Exception) {
            uiLoggingCallback.onLogState("FFMPEG: onNotAvailable ${error.message}", Status.ERROR)
            Log.i("MSG", "FFMPEG_LOG: onNotAvailable ${error.message}")
        }

        override fun onFinish() {
            uiLoggingCallback.onLogState("FFMPEG: onFinish", Status.SUCCESS)
            Log.i("MSG", "FFMPEG_LOG: onFinish")
        }
    }


    /**
     * PlaybackListener
     */
    fun setPlaybackCurrentPosition(currentPosition: Long) {
        currentFrameMsFieldText.postValue(formatTime(currentPosition))
        bestshotMinisec.postValue(convertToMicroseconds(currentPosition))
    }

    /**
     *BtnClickListeners
     */
    fun onBottomSheetOpened() {
        if (attachedMedia.value != null) {
            _runVideoCrop.postValue(Event(Unit))
        }
//        testLog()
    }

    fun testLog() {
        val timer = Timer()
        var counter = 0

        timer.schedule(object : TimerTask() {
            override fun run() {
                val data = LoggingState(
                    counter, "some AI tool log $counter",
                    if (counter > 8) Status.SUCCESS else if (counter % 2 == 0) Status.LOADING else Status.ERROR
                )
                setLogList(data)
                counter++
            }
        }, 0, 2000) // Schedule task with 0 delay and 2-second period
    }

    fun onAnalyzeBtnClick() {
        _runBottomSheet.postValue(Event(Unit))
    }

    fun onAttachFileBtnClick() {
        attachFilesCallback.onAttachFileBtnClick()
    }

    fun onTestAudioBtnClick() {
//        val fileName = "testAudioFile.mp3"
        val fileName = "audio_name_20240102_053427.mp3"

        _playAudio.postValue(Event(fileName))
    }

    fun onTestAudio2BtnClick() {
        val text =
            "Hi from Android Agaaaain! So im testing some audio timestamps from google cloud and creting a new audio, using our elevenlabs!"

        analyzedTextData.postValue(text)
        _runVoiceLab.postValue(Event(Unit))
    }

    fun onRunVideoBtnClick() {
        _createVideo.postValue(Event(Unit))
    }
}