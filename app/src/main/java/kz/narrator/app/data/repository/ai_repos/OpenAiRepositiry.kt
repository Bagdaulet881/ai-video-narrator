package kz.narrator.app.data.repository.ai_repos

import androidx.lifecycle.LiveData
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import kz.narrator.app.data.models.ai_data.AudioRequestPayload
import kz.narrator.app.data.models.network.Resource
import kz.narrator.app.utils.live_data.Event

interface OpenAiRepositiry {
    fun getChatCompletions(chatCompletionRequest: ChatCompletionRequest): LiveData<Event<Resource<ChatCompletion>>>

    fun generateAudio(text: AudioRequestPayload, voiceId: String): LiveData<Event<Resource<String>>>
}