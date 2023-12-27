package kz.narrator.app.ui.ai_processor.text_to_speech

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kz.narrator.app.data.models.AudioRequestPayload
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.data.repository.appeal.AppealRepository
import javax.inject.Inject

class TextToSpeechViewModel
@Inject constructor(
    private val app: Application,
    private val openAiInstance: OpenAiInstance,
    private val openAiRepositiry: OpenAiRepositiry,
    private val appealRepository: AppealRepository,
    private val preferences: Preferences
) : AndroidViewModel(app){

    /**
     * Test ElevenLabs
     */
    private val generateAudio = MutableLiveData<AudioRequestPayload>()
    val audioFilePath = MutableLiveData<String>("audioText.mp3")
    val audioPlayerVisible = MutableLiveData<Boolean>(true)

    val audioAiResource = Transformations.switchMap(generateAudio) {
        openAiRepositiry.generateAudio(voiceId = getVoiceParams(), text = it)
    }

    fun onAudioFileResourceSuccess(filePath: String?){
        audioFilePath.value = filePath?:""
        audioPlayerVisible.value = true
        Log.i("MSG", "AUDIOSAVED: path = ${filePath}")
    }

    fun generateAudioRequest(){
        val testText = "Hello, ohh you are testing my voice from android!"

        val payload = AudioRequestPayload(
//            model_id = "your_model_id",
            text = testText
        )

        generateAudio.postValue(payload)
    }

    fun getVoiceParams(): String {
        val voiceIdDavied: String = "y0DmfPiJ6oiErY1xHRE1"

        return voiceIdDavied
    }
}