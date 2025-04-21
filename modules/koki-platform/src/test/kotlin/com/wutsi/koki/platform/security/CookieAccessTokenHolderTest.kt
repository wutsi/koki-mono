package com.wutsi.koki.platform.security.service

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.security.CookieAccessTokenHolder
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CookieAccessTokenHolderTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()

    private val accessToken = "111"
    private val cookieName = "__ze_cookie__"
    private val cookieTTL = 1000
    private val holder = CookieAccessTokenHolder(
        request = request,
        response = response,
        cookieName = cookieName,
        cookieTimeToLive = cookieTTL,
    )

    @BeforeTest
    fun setUp() {
        doReturn(emptyArray<Cookie>()).whenever(request).cookies
    }

    @Test
    fun get() {
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            Cookie(cookieName, accessToken)
        )
        doReturn(cookies).whenever(request).cookies

        assertEquals(accessToken, holder.get())
    }

    @Test
    fun none() {
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            Cookie("x", "xx")
        )
        doReturn(cookies).whenever(request).cookies

        assertEquals(null, holder.get())
    }

    @Test
    fun set() {
        holder.set(accessToken)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())

        assertEquals(accessToken, cookie.firstValue.value)
        assertEquals(cookieName, cookie.firstValue.name)
        assertEquals(cookieTTL, cookie.firstValue.maxAge)
        assertEquals("/", cookie.firstValue.path)
    }

    @Test
    fun update() {
        val cookie = Cookie(cookieName, "xxx")
        val cookies = arrayOf<Cookie>(
            Cookie("a", "aa"),
            cookie
        )
        doReturn(cookies).whenever(request).cookies

        holder.set(accessToken)

        verify(response).addCookie(cookie)

        assertEquals(accessToken, cookie.value)
        assertEquals(cookieName, cookie.name)
        assertEquals(cookieTTL, cookie.maxAge)
        assertEquals("/", cookie.path)
    }
}
