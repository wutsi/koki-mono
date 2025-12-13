package com.wutsi.koki.platform.util

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.context.i18n.LocaleContextHolder
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Locale
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentTest {
    private val clock = mock<Clock>()
    private var moment: Moment = Moment(clock)

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.ENGLISH)
    private val today = fmt.parse("2020-02-14 15:30:10")

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(Locale.ENGLISH)
        fmt.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Test
    fun now() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Now", moment.format(DateUtils.addSeconds(today, 30)))
    }

    @Test
    fun minutes() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("30 minutes ago", moment.format(DateUtils.addMinutes(today, -30)))
        assertEquals("in 30 minutes", moment.format(DateUtils.addMinutes(today, 30)))
    }

    @Test
    fun hours() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("3 hours ago", moment.format(DateUtils.addHours(today, -3)))
        assertEquals("in 3 hours", moment.format(DateUtils.addHours(today, 3)))
    }

    @Test
    fun days() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Yesterday", moment.format(DateUtils.addDays(today, -1)))
        assertEquals("Tomorrow", moment.format(DateUtils.addDays(today, 1)))
        assertEquals("3 days ago", moment.format(DateUtils.addDays(today, -3)))
        assertEquals("in 3 days", moment.format(DateUtils.addDays(today, 3)))
    }

    @Test
    fun weeks() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Last week", moment.format(DateUtils.addWeeks(today, -1)))
        assertEquals("Next week", moment.format(DateUtils.addWeeks(today, 1)))
        assertEquals("2 weeks ago", moment.format(DateUtils.addWeeks(today, -2)))
        assertEquals("in 2 weeks", moment.format(DateUtils.addWeeks(today, 2)))
    }

    @Test
    fun other() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Dec 14, 2019", moment.format(DateUtils.addMonths(today, -2)))
        assertEquals("Apr 14, 2020", moment.format(DateUtils.addMonths(today, 2)))
    }
}
