package com.wutsi.koki.portal.rest

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccessTokenHolderTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()

    private val accessToken = "111"
    private val holder = AccessTokenHolder()

    @Test
    fun get() {
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            Cookie(AccessTokenHolder.COOKIE_ACCESS_TOKEN, accessToken)
        )
        doReturn(cookies).whenever(request).cookies

        assertEquals(accessToken, holder.get(request))
    }

    @Test
    fun none() {
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            Cookie("x", "xx")
        )
        doReturn(cookies).whenever(request).cookies

        assertNull(holder.get(request))
    }

    @Test
    fun set() {
        holder.set(accessToken, response)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())

        assertEquals(accessToken, cookie.firstValue.value)
        assertEquals(AccessTokenHolder.COOKIE_ACCESS_TOKEN, cookie.firstValue.name)
        assertEquals(AccessTokenHolder.TTL, cookie.firstValue.maxAge)
        assertEquals("/", cookie.firstValue.path)
    }
}
