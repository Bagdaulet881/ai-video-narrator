package kz.narrator.app.ui.main_page

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kz.narrator.app.data.models.AudioRequestPayload
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.data.repository.appeal.AppealRepository
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import kz.narrator.app.utils.base64.Base64Utils.getBase64
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {


    private val _playAudio = MutableLiveData<Event<String>>()
    val playAudio: LiveData<Event<String>>
        get() = _playAudio

    val isMediaEditVisible = MutableLiveData<Boolean>(false) //TODO 881

    /**
     *
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    fun setMediaAttachedStatus(isAttached: Boolean){
        isMediaEditVisible.postValue(isAttached)
    }
    //-----------------------------------
    /**
     *BtnClickListeners
     */
    fun onAttachFileBtnClick() {
        attachFilesCallback.onAttachFileBtnClick()
    }

    fun onSendImageBtnClick(){

    }

    fun onPlayAudioBtnClick(){
//        _playAudio.postValue(Event(audioFilePath.value?:""))
    }

    //-----------------------------------


}