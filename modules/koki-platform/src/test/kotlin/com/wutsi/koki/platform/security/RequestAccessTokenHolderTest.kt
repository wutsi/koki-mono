package com.wutsi.koki.platform.security

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestAccessTokenHolderTest {
    private val request = mock<HttpServletRequest>()

    private val holder = RequestAccessTokenHolder(
        request = request,
    )

    @Test
    fun beader() {
        doReturn("Bearer 12345").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)

        assertEquals("12345", holder.get())
    }

    @Test
    fun apiKey() {
        doReturn("Api-Key 12345").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        assertEquals(null, holder.get())
    }

    @Test
    fun none() {
        doReturn(null).whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        assertEquals(null, holder.get())
    }
}
