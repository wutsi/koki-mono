package com.wutsi.koki.platform.tracing

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class HeaderDeviceIdProviderTest {
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var provider: DeviceIdProvider

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
        provider = HeaderDeviceIdProvider()
    }

    @Test
    fun `return null when attribute not in header`() {
        doReturn(null).whenever(request).getHeader(HttpHeader.DEVICE_ID)

        val value = provider.get(request)
        assertNull(value)
    }

    @Test
    fun `return attribute in header`() {
        doReturn("foo").whenever(request).getHeader(HttpHeader.DEVICE_ID)

        val value = provider.get(request)
        assertEquals("foo", value)
    }

    @Test
    fun set() {
        provider.set("foo", request, response)
        verify(response).setHeader(HttpHeader.DEVICE_ID, "foo")
    }
}
