package kz.narrator.app.data.models.ai_data

data class AudioRequestPayload(
    val model_id: String? = null,
    val text: String,
//    val voice_settings: VoiceSettings
)
