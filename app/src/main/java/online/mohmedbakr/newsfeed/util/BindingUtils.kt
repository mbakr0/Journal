package online.mohmedbakr.newsfeed.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun calculateDate(publicationDate: String?): String {
    if (publicationDate.isNullOrBlank()) return "Unknown"

    return try {
        val published = parseDate(publicationDate)
        val now = Instant.now()

        val seconds = Duration.between(published, now).seconds

        when {
            seconds < 60 -> format(seconds, "second")
            seconds < 3600 -> format(seconds / 60, "minute")
            seconds < 86400 -> format(seconds / 3600, "hour")
            seconds < 604800 -> format(seconds / 86400, "day")
            seconds < 2419200 -> format(seconds / 604800, "week")
            seconds < 29030400 -> format(seconds / 2419200, "month")
            else -> format(seconds / 29030400, "year")
        }

    } catch (_: Exception) {
       publicationDate
    }
}

fun parseDate(date: String): Instant {
    val rfcFormatter = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ENGLISH)

    return try {
        ZonedDateTime.parse(date, rfcFormatter).toInstant()
    } catch (_: DateTimeParseException) {
        try {
            val usFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a", Locale.ENGLISH)
            val local = LocalDateTime.parse(date, usFormatter)
            local.atZone(ZoneId.systemDefault()).toInstant()
        } catch (_: Exception) {
            throw IllegalArgumentException("Unsupported date format: $date")
        }
    }
}

private fun format(value: Long, unit: String): String {
    return if (value == 1L) "$value $unit ago"
    else "$value ${unit}s ago"
}
