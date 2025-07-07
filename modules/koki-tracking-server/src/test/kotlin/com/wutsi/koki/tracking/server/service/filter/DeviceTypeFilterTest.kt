package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.tracking.server.domain.TrackEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceTypeFilterTest {
    private val filter = DeviceTypeFilter(DefaultKVLogger())

    @Test
    fun empty() {
        val track = createTrack("")
        assertEquals(DeviceType.UNKNOWN, filter.filter(track).deviceType)
    }

    @Test
    fun desktop() {
        val track =
            createTrack("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
        assertEquals(DeviceType.DESKTOP, filter.filter(track).deviceType)
    }

    @Test
    fun mobileIPhone() {
        val track =
            createTrack("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/11.0 Mobile/14E304 Safari/602.1")
        assertEquals(DeviceType.MOBILE, filter.filter(track).deviceType)
    }

    @Test
    fun mobileAndroid() {
        val track =
            createTrack("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")
        assertEquals(DeviceType.MOBILE, filter.filter(track).deviceType)
    }

    @Test
    fun tabletIPad() {
        val track =
            createTrack("Mozilla/5.0 (iPad; CPU OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148")
        assertEquals(DeviceType.TABLET, filter.filter(track).deviceType)
    }

    @Test
    fun tabletAndroid() {
        val track =
            createTrack("Dalvik/2.1.0 (Linux; U; Android 14; SM-X306B Build/UP1A.231005.007)")
        assertEquals(DeviceType.TABLET, filter.filter(track).deviceType)
    }

    @Test
    fun app() {
        val track = createTrack("Dart/2.16 (dart:io)")
        assertEquals(DeviceType.MOBILE, filter.filter(track).deviceType)
    }

    @Test
    fun bot() {
        val track = createTrack("Googlebot/2.1 (+http://www.google.com/bot.html)")
        assertEquals(DeviceType.UNKNOWN, filter.filter(track).deviceType)
    }

    @Test
    fun noUserAgent() {
        val track = createTrack(null)
        assertEquals(DeviceType.UNKNOWN, filter.filter(track).deviceType)
    }

    private fun createTrack(ua: String?) = TrackEntity(
        ua = ua,
    )
}
