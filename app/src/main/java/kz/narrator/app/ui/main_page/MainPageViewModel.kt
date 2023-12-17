package kz.narrator.app.ui.main_page

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
    fun onTestRequest(){
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant!"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "Hello! Im testing that from android!"
                )
            )
        )

        sendRequestOpenAi.postValue(chatCompletionRequest)
//        viewModelScope.launch {
//            Log.i("MSG", "SENDREQUEST: Loading")
////            val completion: ChatCompletion = openAiInstance.get().chatCompletion(chatCompletionRequest)
//            val completions: Flow<ChatCompletionChunk> = openAiInstance.get().chatCompletions(chatCompletionRequest).flowOn(Dispatchers.IO)
//
//            val test = completions.collect{
//                it.choices.get(0)
//            }
//            Log.i("MSG", "SENDREQUEST: After ${test}")
//        }


//        val completions: Flow<ChatCompletionChunk> = openAi.get().chatCompletions(chatCompletionRequest)

//        viewModelScope.launch {
//            openAiRepositiry.getChatCompletions(chatCompletionRequest)
//                .flowOn(Dispatchers.IO)
//                .collect {
//                    when(it.status){
//                        Status.LOADING -> {
//                            Log.i("MSG", "SENDREQUEST: Loading")
//                        }
//                        Status.ERROR -> {
//                            Log.i("MSG", "SENDREQUEST: Error")
//                        }
//                        Status.SUCCESS -> {
//                            Log.i("MSG", "SENDREQUEST: Success ${it.data?.choices?.get(0)}")
//                        }
//                    }
//                }
//        }
    }

    /**
     * Test ElevenLabs
     */
    private val generateAudio = MutableLiveData<AudioRequestPayload>()

    val audioAiResource = Transformations.switchMap(generateAudio) {
        openAiRepositiry.generateAudio(voiceId = getVoiceParams(), text = it)
    }

    fun onAudioFileResourceSuccess(filePath: String?){
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
    fun onOpenAiResourceSuccess(data: ChatCompletion?) {
        val text = data?.choices?.get(0)
        Log.i("MSG", "SENDREQUEST: ${text}")
        responseFieldText.postValue(text?.message?.content)
    }
    /**
     *
     */
    fun onSendImageBtnClick(){
        if(attachFilesCallback.getAttachedFiles().isNotEmpty()){
            val test = getImageParams()
            Log.i("MSG", "SENDBTNCLICK: ${test.size}")
            Log.i("MSG", "SENDBTNCLICK: ${test.get(0).uri}")
//            chatRepository.sendMessage(
//                message = message,
//                attachedFiles = getImageParams()
//            )
        }
    }

    fun getImageParams(): MutableList<Scan> {
        val temp = mutableListOf<Scan>()
        attachFilesCallback.getAttachedFiles().forEach {
            if (it is Scan) {
                temp.add(it)
            }
        }

        return temp
    }

    fun onAttachFileBtnClick() {
        attachFilesCallback.onAttachFileBtnClick()
    }

    //-----------------------------------
    fun getVoiceParams(): String {
        val voiceIdDavied: String = "y0DmfPiJ6oiErY1xHRE1"

        return voiceIdDavied
    }
}