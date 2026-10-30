package kz.narrator.app.network.api

import kz.narrator.app.BuildConfig
import kz.narrator.app.data.models.ai_data.AudioRequestPayload
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AiApi {

    @Headers(
        "Accept: audio/mpeg",
        "Content-Type: application/json",
        "isAuthorizable: false"
    )
    @POST("v1/text-to-speech/{voice_id}")
    suspend fun generateAudio(
        @Path("voice_id") voiceId: String,
        @Body textBody: AudioRequestPayload,
        @Header("xi-api-key") apiKey: String = BuildConfig.ELEVENLABS_API_KEY
    ): Response<ResponseBody>

}