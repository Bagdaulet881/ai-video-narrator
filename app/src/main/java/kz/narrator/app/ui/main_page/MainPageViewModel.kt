package kz.narrator.app.ui.main_page

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.narrator.app.data.models.ai_data.*
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import kz.narrator.app.ui_common.callbacks.FFMpegCallback
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
import kz.narrator.app.ui_common.callbacks.RecyclerViewItemClickCallback
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import kz.narrator.app.ui_common.video_tools.DefaultScriptsSettings
import kz.narrator.app.utils.convertToMicroseconds
import kz.narrator.app.utils.formatTime
import kz.narrator.app.utils.live_data.Event
import java.util.*
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    private val _listOfSettings = MutableLiveData<List<Any>>()
    val listOfSettings: LiveData<List<Any>>
        get() = _listOfSettings

    private val _listOfLogs = MutableLiveData<List<Any>>()
    val listOfLogs: LiveData<List<Any>>
        get() = _listOfLogs

    private val _playAudio = MutableLiveData<Event<String>>()
    val playAudio: LiveData<Event<String>>
        get() = _playAudio

    private val _runBottomSheet = MutableLiveData<Event<String>>()
    val runBottomSheet: LiveData<Event<String>>
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

    private val _runGoogleAi = MutableLiveData<Event<Unit>>()
    val runGoogleAi: LiveData<Event<Unit>>
        get() = _runGoogleAi

    private val _runResultScreen = MutableLiveData<Event<Unit>>()
    val runResultScreen: LiveData<Event<Unit>>
        get() = _runResultScreen

    private val _createVideo = MutableLiveData<Event<Unit>>()
    val createVideo: LiveData<Event<Unit>>
        get() = _createVideo

    val isMediaEditVisible = MutableLiveData<Boolean>(false)
    val isMediaCreateVisible = MutableLiveData<Boolean>(false)
    val isTestVisible = MutableLiveData<Boolean>(false)

    val currentFrameMsFieldText = MutableLiveData<String>()

    //Settings DATA
    val scriptSelected = MutableLiveData<ScriptAudioSettings>()

    //Api DATA
    val bestshotMinisec = MutableLiveData<Long>()
    val attachedMediaData = MutableLiveData<Scan>()

    val croppedImagesData = MutableLiveData<List<Bitmap>>()
    val analyzedTextData = MutableLiveData<String>()
    val audioFileNameData = MutableLiveData<String>()
    val audioFileTimeStampsData = MutableLiveData<List<MappedTimeStamps>>()

    init {
        if(preferences.getVoiceScripts() == null){
            preferences.setVoiceScripts(DefaultScriptsSettings.getDefaultList())
        }
        if(preferences.getSelectedScript() == null){
            scriptSelected.postValue(DefaultScriptsSettings.getDefaultSelect())
            preferences.setSelectedScript(DefaultScriptsSettings.getDefaultSelect())
        }
    }
    /**
     *CALLBACK_ UI Log
     */
    val uiLoggingCallback = object : UILoggingCallback {
        override fun onTimeStampSuccess(sentences: List<MappedTimeStamps>) {
            Log.i("MSG", "MAIN_VM_UI_LOG: onTimeStampSuccess ${sentences}")
            audioFileTimeStampsData.postValue(sentences)
            showHideCreateVideo(true)
        }

        override fun onLogState(msg: String, status: Status) {
            val sz = _listOfLogs.value?.lastIndex
            val cnt = if(sz == null || _listOfLogs.value?.isEmpty() == true) 0 else (_listOfLogs.value?.get(sz) as LoggingState).id + 1

            setLogList(
                LoggingState(
                    id = cnt,
                    msg = msg,
                    status = status
                )
            )
            Log.i("MSG", "MAIN_VM_UI_LOG: ${msg}")
        }

        override fun onProgressState(id: Int, msg: String, status: Status) {
            setLogList(
                LoggingState(
                    id = id,
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

    fun setSettingsList(){
        val temp = mutableListOf<Any>()

        temp.add(
            SettingsColoredValue(
                title = "Text color",
                value = "#FFE880"
            )
        )

        temp.add(
            SettingsTitleValue(
                title = "Text size",
                value = "34dp"
            )
        )

        temp.add(
            SettingsTitleValue(
                title = "Analyzed text",
                value = analyzedTextData.value?:""
            )
        )

        temp.add(
            SettingsTitleValue(
                title = "Analyzed text timestamps",
                value = (audioFileTimeStampsData.value?.size?:0).toString()
            )
        )

        temp.add(
            SettingsTitleValue(
                title = "Audio status",
                value = if(audioFileNameData.value?.isNotEmpty() == true) "Loaded!" else "Not found."
            )
        )

        temp.add(
            SettingsTitleValue(
                title = "Video status",
                value = if(attachedMediaData.value?.uri != null) "Exists!" else "Not found."
            )
        )

        _listOfSettings.postValue(temp)
    }

    /**
     *CALLBACK_ Main
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
                        mediaFilePath = attachedMediaData.value!!.uri,
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

            if(analyzedText.contains("sorry, but I can't provide information") || analyzedText.contains("я не могу обсудить")){
                uiLoggingCallback.onLogState("OpenAi: onImageAnalyzed can't provide information", Status.ERROR)
            }else{
                uiLoggingCallback.onLogState("MAIN: onImageAnalyzed", Status.SUCCESS)
                analyzedTextData.postValue(analyzedText)
                _runVoiceLab.postValue(Event(Unit))
            }
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
            _runGoogleAi.postValue(Event(Unit))
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

        override fun getVoiceIdSettings(): String {
            return preferences.getSelectedScript()?.voiceId?:""
        }

        override fun getSystemScript(): String {
            return preferences.getSelectedScript()?.script?:""
        }

        override fun getCommandScript(): String {
            return preferences.getSelectedScript()?.command?:""
        }

        override fun getLanguageSettings(): String {
            return preferences.getSelectedScript()?.language?:""
        }

    }

    /**
     *CALLBACK_ AttachFiles
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    fun setMediaAttachedStatus(isAttached: Boolean) {
        val media = attachFilesCallback.getAttachedFiles().get(0) as Scan
        attachedMediaData.postValue(media)
        Log.i("MSG", "FFMPEG_LOG: media attached")
        _runVideoPlayer.postValue(Event(media.uri))
        showHideEditVideo(isAttached)
    }

    /**
     *CALLBACK_ FFMPEG
     */
    val ffMpegCallback = object : FFMpegCallback {
        override fun onProgress(progress: String) {
//            val size = extractSizeFromLog(progress)
//            uiLoggingCallback.onProgressState(881, "FFMPEG: size = $size", Status.LOADING)
            Log.i("MSG", "FFMPEG_LOG: onProgress ${progress}")
        }

        override fun onSuccess(msg: String) {
            uiLoggingCallback.onLogState(
                "FFMPEG: video create",
                Status.SUCCESS
            )
            uiLoggingCallback.onLogState(
                "FFMPEG: DOWNLOADED to Gallery",
                Status.SUCCESS
            )
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

        override fun onLogStatus(msg: String, status: Status) {
            uiLoggingCallback.onLogState("FFMPEG: ${msg}", status)
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
     * States
     */

    fun showHideEditVideo(state: Boolean){
        onSetScriptBtnClick(preferences.getSelectedScript()?:DefaultScriptsSettings.getDefaultSelect())
        isMediaEditVisible.postValue(state)
        isMediaCreateVisible.postValue(false)
    }

    fun showHideCreateVideo(state: Boolean){
        isMediaCreateVisible.postValue(state)
        isMediaEditVisible.postValue(false)
        setSettingsList()
    }
    /**
     *
     */
    val recyclerViewItemClickCallback = object : RecyclerViewItemClickCallback {

        override fun onRecyclerViewItemClick(any: Any) {

        }

    }

    /**
     *BtnClickListeners
     */
    fun onSetScriptBtnClick(selected: ScriptAudioSettings){
        scriptSelected.postValue(selected)
        preferences.setSelectedScript(selected)
    }

    fun onClearLogs(){
        _listOfLogs.postValue(emptyList())
    }

    fun onBottomSheetOpened(type: String) {
        when(type){
            "analyze" -> {
                if (attachedMediaData.value != null) {
                    _runVideoCrop.postValue(Event(Unit))
                }
            }
            "create" -> {
                _createVideo.postValue(Event(Unit))
            }
        }
    }

    fun onAnalyzeBtnClick() {
        _runBottomSheet.postValue(Event("analyze"))
    }

    fun onCreateVideoBtnClick() {
        _runBottomSheet.postValue(Event("create"))
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
}