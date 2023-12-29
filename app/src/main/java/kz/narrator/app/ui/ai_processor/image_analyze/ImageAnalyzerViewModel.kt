package kz.narrator.app.ui.ai_processor.image_analyze

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role.Companion.Assistant
import com.aallam.openai.api.model.ModelId
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.data.repository.appeal.AppealRepository
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
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

    fun onRun(){
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
                content = "You are Sir David Attenborough. Narrate the picture of the human as if it is a nature documentary. Make it snarky and funny. Don't repeat yourself. Make it short. If I do anything remotely interesting, make a big deal about it!"
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

        return requestScripts
    }

    fun getChatGPTParams(): List<ContentPart> {
        val commands = mutableListOf<ContentPart>()
        val attachedImages = mainCallback.getCroppedImages()
        commands.add(
            TextPart(
                text = "Describe this image"
            )
        )
        commands.add(
            ImagePart(
                imageUrl = ImagePart.ImageURL(
//                    url = "data:image/jpeg;base64,\"${convertImageToBase64(app.applicationContext, attachedImages.get(0).uri)}\"".replace("\n", "")
                    url = "data:image/jpeg;base64,${
                        Base64Utils.convertToBase64(attachedImages.get(0))
                    }"
                    //if error then use getSmallBitmap
//                    "data:image/jpeg;base64,${
//                        Base64Utils.getBase64(
//                            app.applicationContext,
//                            attachedImages.get(0).uri
//                        )
//                    }"
                )
            )
        )

        return commands.toList()
    }
}