package kz.narrator.app.data.enums.app

enum class DatePatternEnums(val id: String) {

    YYYYMMDD_BY_DASH_HHMMSS_BY_COLON("yyyy-MM-dd HH:mm:ss"),
    DDMMYYYY_BY_PERIOD_HHMM_BY_COLON("dd.MM.yyyy HH:mm"),
    YYYYMMDD_BY_DASH("yyyy-MM-dd"),
    DDMMYYYY_BY_PERIOD("dd.MM.yyyy"),
    MMSS_BY_COLON("mm:ss"),

    DATE_FOR_PROFILE("dd MMM yyyyг."),
    yyyyMMdd_HHmmss("yyyyMMdd_HHmmss")

}