package com.wutsi.koki.portal.service

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccessTokenHolderTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()

    private val accessToken = "111"
    private val holder = AccessTokenHolder()

    @BeforeTest
    fun setUp() {
        doReturn(emptyArray<Cookie>()).whenever(request).cookies
    }

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
        holder.set(accessToken, request, response)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())

        assertEquals(accessToken, cookie.firstValue.value)
        assertEquals(AccessTokenHolder.COOKIE_ACCESS_TOKEN, cookie.firstValue.name)
        assertEquals(AccessTokenHolder.TTL, cookie.firstValue.maxAge)
        assertEquals("/", cookie.firstValue.path)
    }

    @Test
    fun update() {
        val cookie = Cookie(AccessTokenHolder.COOKIE_ACCESS_TOKEN, "xxx")
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            cookie
        )
        doReturn(cookies).whenever(request).cookies

        holder.set(accessToken, request, response)

        verify(response).addCookie(cookie)

        assertEquals(accessToken, cookie.value)
        assertEquals(AccessTokenHolder.COOKIE_ACCESS_TOKEN, cookie.name)
        assertEquals(AccessTokenHolder.TTL, cookie.maxAge)
        assertEquals("/", cookie.path)
    }
}
