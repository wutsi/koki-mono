package com.wutsi.koki.platform.geoip

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.mock
import org.springframework.cache.Cache
import kotlin.test.Test
import kotlin.test.assertEquals

class GeoIpServiceTest {
    private val cache = mock<Cache>()
    private val service = GeoIpService(cache)

    @Test
    fun `fetch from server`() {
        doReturn(null).whenever(cache).get(any<String>(), any<Class<GeoIp>>())

        val result = service.resolve("198.175.56.97")
        verify(cache).put("geoip.198.175.56.97", result)
    }

    @Test
    fun `fetch from cache`() {
        val geoip = GeoIp()
        doReturn(geoip).whenever(cache).get(any<String>(), any<Class<GeoIp>>())

        val result = service.resolve("198.175.56.97")
        assertEquals(geoip, result)
    }

    @Test
    fun `cache get error`() {
        doThrow(IllegalStateException::class).whenever(cache).get(any<String>(), any<Class<GeoIp>>())

        val result = service.resolve("198.175.56.97")
        verify(cache).put("geoip.198.175.56.97", result)
    }

    @Test
    fun `cache put error`() {
        doReturn(null).whenever(cache).get(any<String>(), any<Class<GeoIp>>())
        doThrow(IllegalStateException::class).whenever(cache).put(any<String>(), any<GeoIp>())

        val result = service.resolve("198.175.56.97")
        verify(cache).put("geoip.198.175.56.97", result)
    }

    @Test
    fun `invalid ip`() {
        val result = service.resolve("this is an invalid ip")
        assertEquals(null, result)
    }
}
