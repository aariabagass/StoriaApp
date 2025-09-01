package com.ariabagas.storiaapp.utils

import android.text.format.DateUtils
import java.time.Instant
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun getTimeAgo(isoTime: String?): CharSequence {
        if (isoTime.isNullOrBlank()) return ""

        return try {
            val formatter = DateTimeFormatter.ISO_INSTANT
            val instant = Instant.from(formatter.parse(isoTime))
            val timeMillis = instant.toEpochMilli()

            DateUtils.getRelativeTimeSpanString(
                timeMillis,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )
        } catch (e: Exception) {
            ""
        }
    }
}

