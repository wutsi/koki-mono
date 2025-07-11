package com.wutsi.koki.tracking.server.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class CountryFilterTest {
    private val ipApi = mock<GeoIpService>()
    private val filter = CountryFilter(ipApi, DefaultKVLogger())

    @Test
    fun filter() {
        doReturn(GeoIp(countryCode = "CM")).whenever(ipApi).resolve(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertEquals("CM", track.country)
    }

    @Test
    fun `ip null`() {
        val track = filter.filter(createTrack(ip = null))

        verify(ipApi, never()).resolve(any())
        assertNull(track.country)
    }

    @Test
    fun `ip empty`() {
        val track = filter.filter(createTrack(ip = ""))

        verify(ipApi, never()).resolve(any())
        assertNull(track.country)
    }

    @Test
    fun exception() {
        doThrow(RuntimeException::class).whenever(ipApi).resolve(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertNull(track.country)
    }

    @Test
    fun `null`() {
        doReturn(null).whenever(ipApi).resolve(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertNull(track.country)
    }

    private fun createTrack(
        url: String? = null,
        referrer: String? = null,
        ua: String? = null,
        ip: String? = null,
    ) = TrackEntity(
        url = url,
        referrer = referrer,
        ua = ua,
        ip = ip,
    )
}
