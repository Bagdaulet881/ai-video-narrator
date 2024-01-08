package kz.narrator.app.ui.ai_processor.image_analyze

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import kz.narrator.app.utils.base64.Base64Utils
import javax.inject.Inject

class ImageAnalyzerViewModel
@Inject constructor(
    private val app: Application,
    private val openAiRepositiry: OpenAiRepositiry
) : AndroidViewModel(app) {

    val responseFieldText = MutableLiveData<String>()
    val oldScripts = mutableListOf<ChatMessage>()

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

    fun onRun(){
        resultLoggingCallback.onLogState("OpenAI: initializing", Status.SUCCESS)
        generateChatGptRequest()
    }
    /**
     * Test OpenAi
     */
    private val sendRequestOpenAi = MutableLiveData<ChatCompletionRequest>()

    val openAiResource = Transformations.switchMap(sendRequestOpenAi) {
        openAiRepositiry.getChatCompletions(it)
    }

    fun generateChatGptRequest() {
        resultLoggingCallback.onLogState("OpenAI: making requests", Status.LOADING)

        val chatCompletionRequest = ChatCompletionRequest(
            //gpt-3.5-turbo
            model = ModelId("gpt-4-vision-preview"),
            messages = getScriptParams(),
            maxTokens = 500
        )

        sendRequestOpenAi.postValue(chatCompletionRequest)
    }

    fun onOpenAiResourceSuccess(data: ChatCompletion?) {
        val text = data?.choices?.get(0)
        Log.i("MSG", "SENDREQUEST: ${text}")
        oldScripts.add(
            ChatMessage(
                role = ChatRole.Assistant,
                content = text?.message?.content
            )
        )
        mainCallback.onImageAnalyzed(text?.message?.content?:"")
        responseFieldText.postValue(text?.message?.content)
    }

    /**
     *
     */

    fun getScriptParams(): MutableList<ChatMessage> {
        val requestScripts = mutableListOf<ChatMessage>()

        requestScripts.add(
            ChatMessage(
                role = ChatRole.System,
                content = mainCallback.getSystemScript()
            )
        )
        requestScripts.addAll(
            oldScripts
        )
        requestScripts.add(
            ChatMessage(
                role = ChatRole.User,
                content = getChatGPTParams()
            )
        )
        resultLoggingCallback.onLogState("OpenAI: get chatGPT scripts", Status.SUCCESS)

        return requestScripts
    }

    fun getChatGPTParams(): List<ContentPart> {
        val commands = mutableListOf<ContentPart>()
        val attachedImages = mainCallback.getCroppedImages()
        commands.add(
            TextPart(
                text = mainCallback.getCommandScript()
            )
        )
        commands.add(
            ImagePart(
                imageUrl = ImagePart.ImageURL(
//                    url = "data:image/jpeg;base64,\"${convertImageToBase64(app.applicationContext, attachedImages.get(0).uri)}\"".replace("\n", "")
                    url = "data:image/jpeg;base64,${
                        Base64Utils.convertToBase64(attachedImages.get(0))
                    }"
                )
            )
        )

        resultLoggingCallback.onLogState("OpenAI: get chatGPT params", Status.SUCCESS)
        return commands.toList()
    }
}