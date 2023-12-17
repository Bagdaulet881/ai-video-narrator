package kz.narrator.app.data.repository.ai_repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kz.narrator.app.data.models.network.Resource
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.utils.live_data.Event
import retrofit2.Response
import javax.inject.Inject

class OpenAiRepositiryImpl
@Inject constructor(
    private val openAiInstance: OpenAiInstance
): OpenAiRepositiry {

    override fun getChatCompletions(chatCompletionRequest: ChatCompletionRequest): LiveData<Event<Resource<ChatCompletion>>> {
        return liveData {
            try {
                emit(
                    Event(Resource.loading(null))
                )
                val request = openAiInstance.get().chatCompletion(chatCompletionRequest)

                emit(
                    Event(Resource.success(request))
                )
            }catch (e: Exception){
                emit(
                    Event(Resource.error("TESTFLOW_ERROR", null, e))
                )
            }
        }
    }
}