package kz.narrator.app.data.models.ai_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class SettingsTitleValue(
    val title: String,
    val value: String
)

data class SettingsColoredValue(
    val title: String,
    val value: String
)

@Parcelize
data class ScriptAudioSettings(
    val name: String,
    val voiceId: String,
    val language: String,
    val script: String,
    val command: String
): Parcelable