package com.wutsi.koki.portal.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

@Service
class Moment(
    private val clock: Clock,
    private val messages: MessageSource,
) {
    fun format(date: Date?): String {
        if (date == null) {
            return ""
        }

        val now = toLocalDateTime(Date(clock.millis()))
        val localDate = toLocalDateTime(date)
        val minutes = ChronoUnit.MINUTES.between(now, localDate)
        val hours = ChronoUnit.HOURS.between(now, localDate)
        val days = ChronoUnit.DAYS.between(now, localDate)
        val weeks = ChronoUnit.WEEKS.between(now, localDate)

        if (minutes == 0L) {
            return getMessage("moment.now")
        } else if (Math.abs(minutes) < 60) {
            return if (minutes < 0) {
                getMessage(
                    "moment.ago_minutes",
                    arrayOf(-minutes),
                )
            } else {
                getMessage("moment.in_minutes", arrayOf(minutes))
            }
        } else if (Math.abs(hours) < 24) {
            return if (hours < 0) {
                getMessage("moment.ago_hours", arrayOf(-hours))
            } else {
                getMessage(
                    "moment.in_hours",
                    arrayOf(hours),
                )
            }
        } else if (days == 0L) {
            return getMessage("moment.today")
        } else if (days == -1L) {
            return getMessage("moment.yesterday")
        } else if (days == 1L) {
            return getMessage("moment.tomorrow")
        } else if (days > -7L) {
            return getMessage("moment.ago_days", arrayOf(-days))
        } else if (days > 1 && days < 7L) {
            return getMessage("moment.in_days", arrayOf(days))
        } else if (weeks >= -4L) {
            return getMessage("moment.ago_weeks", arrayOf(-weeks))
        } else if (weeks <= 4L) {
            return getMessage("moment.in_weeks", arrayOf(weeks))
        } else {
            val fmt = DateFormat.getDateInstance(DateFormat.MEDIUM, LocaleContextHolder.getLocale())
            return fmt.format(date)
        }
    }

    private fun toLocalDateTime(date: Date): LocalDateTime {
        return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime()
    }

    fun getMessage(key: String, args: Array<Any>? = null, locale: Locale? = null): String {
        val loc = locale ?: LocaleContextHolder.getLocale()
        return messages.getMessage(key, args, loc)
    }
}
