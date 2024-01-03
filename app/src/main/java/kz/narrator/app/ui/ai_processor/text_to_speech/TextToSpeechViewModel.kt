package kz.narrator.app.ui.ai_processor.text_to_speech

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kz.narrator.app.data.models.ai_data.AudioRequestPayload
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.data.repository.appeal.AppealRepository
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import javax.inject.Inject

class TextToSpeechViewModel
@Inject constructor(
    private val app: Application,
    private val openAiRepositiry: OpenAiRepositiry,
) : AndroidViewModel(app){
    /**
     *
     */
    private lateinit var mainCallback: MainMediaCallback

    fun setMainMediaCallback(callback: MainMediaCallback) {
        this.mainCallback = callback
    }

    private lateinit var resultLoggingCallback: UILoggingCallback

    fun setUILoggingCallback(callback: UILoggingCallback) {
        this.resultLoggingCallback = callback
    }
    /**
     * Test ElevenLabs
     */
    private val generateAudio = MutableLiveData<AudioRequestPayload>()

    val audioAiResource = Transformations.switchMap(generateAudio) {
        openAiRepositiry.generateAudio(voiceId = getVoiceParams(), text = it)
    }

    fun onAudioFileResourceSuccess(fileName: String?){
        mainCallback.onTextToVoice(fileName?:"")
        Log.i("MSG", "AUDIOSAVED: path = ${fileName}")
    }

    fun onRun(){
        val text = mainCallback.getAnalyzedText()
        resultLoggingCallback.onLogState("ElevenLabs: initializing", Status.SUCCESS)

        generateAudioRequest(text)
    }

    fun generateAudioRequest(text: String) {
        resultLoggingCallback.onLogState("ElevenLabs: request", Status.LOADING)

        val payload = AudioRequestPayload(
//            model_id = "your_model_id",
            text = text
        )

        generateAudio.postValue(payload)
    }

    fun getVoiceParams(): String {
        val voiceIdDavied: String = "y0DmfPiJ6oiErY1xHRE1"
        resultLoggingCallback.onLogState("ElevenLabs: selected voiceID", Status.SUCCESS)

        return voiceIdDavied
    }
}