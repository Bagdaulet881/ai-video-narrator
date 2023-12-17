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
import kz.narrator.app.utils.base64.Base64Utils
import kz.narrator.app.utils.base64.Base64Utils.convertImageToBase64
import kz.narrator.app.utils.base64.Base64Utils.getBase64
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application,
    private val openAiInstance: OpenAiInstance,
    private val openAiRepositiry: OpenAiRepositiry,
    private val appealRepository: AppealRepository,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    val responseFieldText = MutableLiveData<String>()

    private val _playAudio = MutableLiveData<Event<String>>()
    val playAudio: LiveData<Event<String>>
        get() = _playAudio

    /**
     *
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    /**
     * Test OpenAi
     */
    private val sendRequestOpenAi = MutableLiveData<ChatCompletionRequest>()

    val openAiResource = Transformations.switchMap(sendRequestOpenAi) {
        openAiRepositiry.getChatCompletions(it)
    }
    fun generateChatGptRequest(){
        val chatCompletionRequest = ChatCompletionRequest(
            //gpt-3.5-turbo
            model = ModelId("gpt-4-vision-preview"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are Sir David Attenborough. Narrate the picture of the human as if it is a nature documentary. Make it snarky and funny. Don't repeat yourself. Make it short. If I do anything remotely interesting, make a big deal about it!"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = getChatGPTParams()
                )
//                ChatMessage(
//                    role = ChatRole.User,
//                    content = "Hello! Im testing that from android!"
//                )
            ),
            maxTokens = 500
        )

        sendRequestOpenAi.postValue(chatCompletionRequest)
    }

    fun onOpenAiResourceSuccess(data: ChatCompletion?) {
        val text = data?.choices?.get(0)
        Log.i("MSG", "SENDREQUEST: ${text}")
        responseFieldText.postValue(text?.message?.content)
    }
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

    //-----------------------------------
    /**
     *BtnClickListeners
     */
    fun onAttachFileBtnClick() {
        attachFilesCallback.onAttachFileBtnClick()
    }

    fun onSendImageBtnClick(){
        if(attachFilesCallback.getAttachedFiles().isNotEmpty()){
            val test = getImageParams()
            Log.i("MSG", "SENDBTNCLICK: ${test.size}")
            Log.i("MSG", "SENDBTNCLICK: ${test.get(0).uri}")

            generateChatGptRequest()
        }
    }

    fun onPlayAudioBtnClick(){
        _playAudio.postValue(Event(audioFilePath.value?:""))
    }

    //-----------------------------------
    fun getImageParams(): List<Scan> {
        val temp = mutableListOf<Scan>()
        attachFilesCallback.getAttachedFiles().forEach {
            if (it is Scan) {
                temp.add(it)
            }
        }

        return temp.toList()
    }

    fun getVoiceParams(): String {
        val voiceIdDavied: String = "y0DmfPiJ6oiErY1xHRE1"

        return voiceIdDavied
    }

    fun getChatGPTParams(): List<ContentPart> {
        val commands = mutableListOf<ContentPart>()
        val attachedImages = getImageParams()
        commands.add(
            TextPart(
                text = "Describe this image"
            )
        )
        commands.add(
            ImagePart(
                imageUrl = ImagePart.ImageURL(
//                    url = "data:image/jpeg;base64,\"${convertImageToBase64(app.applicationContext, attachedImages.get(0).uri)}\"".replace("\n", "")
                url = "data:image/jpeg;base64,${getBase64(app.applicationContext, attachedImages.get(0).uri)}"
                )
            )
        )

        return commands.toList()
    }
}