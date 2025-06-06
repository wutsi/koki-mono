package com.wutsi.koki.tracking.server.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.IpApiService
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class CountryFilterTest {
    private val ipApi = mock<IpApiService>()
    private val filter = CountryFilter(ipApi)

    @Test
    fun filter() {
        doReturn("CM").whenever(ipApi).resolveCountry(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertEquals("CM", track.country)
    }

    @Test
    fun noIp() {
        val track = filter.filter(createTrack(ip = null))

        verify(ipApi, never()).resolveCountry(any())
        assertNull(track.country)
    }

    @Test
    fun exception() {
        doThrow(RuntimeException::class).whenever(ipApi).resolveCountry(any())

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
