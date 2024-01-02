package kz.narrator.app.data.models.ai_data

data class MappedTimeStamps(
    var word: String? = null,
    var startTime: Long? = null,
    var startTimeNanos: Int? = null,
    var endTime: Long? = null,
    var endTimeNanos: Int? = null
)