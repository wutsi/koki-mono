package com.wutsi.koki.tracking.server.service

import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class IpApiServiceTest {
    private val rest = mock<RestTemplate>()
    private val service = IpApiService(rest)
    private val ip = "1.1.1.1.1"

    @Test
    fun resolveCountry() {
        doReturn(
            ResponseEntity(
                mapOf("countryCode" to "US"),
                HttpStatus.OK,
            )
        ).whenever(rest).getForEntity("http://ip-api.com/json/$ip", Map::class.java)

        val country = service.resolveCountry(ip)

        assertEquals("US", country)
    }

    @Test
    fun noCountry() {
        doReturn(
            ResponseEntity(
                emptyMap<String, String>(),
                HttpStatus.OK,
            )
        ).whenever(rest).getForEntity("http://ip-api.com/json/$ip", Map::class.java)

        val country = service.resolveCountry(ip)

        assertEquals(null, country)
    }
}
