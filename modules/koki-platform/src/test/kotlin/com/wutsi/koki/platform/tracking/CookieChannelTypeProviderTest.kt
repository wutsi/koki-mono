package com.wutsi.koki.platform.tracking

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.track.dto.ChannelType
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class CookieChannelTypeProviderTest {
    companion object {
        const val COOKIE_NAME: String = "_cookie"
    }

    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var provider: CookieChannelTypeProvider

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
        provider = CookieChannelTypeProvider(COOKIE_NAME)
    }

    @Test
    fun `return null when cookie and attribute are null`() {
        doReturn(null).whenever(request).cookies
        doReturn(null).whenever(request).getAttribute(COOKIE_NAME)

        val value = provider.get(request)
        assertEquals(ChannelType.UNKNOWN, value)
    }

    @Test
    fun `return cookie when cookie not found and attribute is null`() {
        doReturn(
            arrayOf(
                Cookie("foo1", "bar1"),
                Cookie("foo2", "bar2"),
                Cookie("foo3", "bar3"),
            ),
        ).whenever(request).cookies
        doReturn(null).whenever(request).getAttribute(COOKIE_NAME)

        val value = provider.get(request)
        assertEquals(ChannelType.UNKNOWN, value)
    }

    @Test
    fun `return value from attribute when cookie not found`() {
        doReturn(null).whenever(request).cookies
        doReturn(ChannelType.SOCIAL).whenever(request).getAttribute(COOKIE_NAME)

        val value = provider.get(request)
        assertEquals(ChannelType.SOCIAL, value)
    }

    @Test
    fun set() {
        provider.set(ChannelType.SOCIAL, request, response)

        verify(request).setAttribute(COOKIE_NAME, ChannelType.SOCIAL.name)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals(COOKIE_NAME, cookie.firstValue.name)
        assertEquals(ChannelType.SOCIAL.name, cookie.firstValue.value)
    }

    @Test
    fun update() {
        val duid = Cookie(COOKIE_NAME, ChannelType.MESSAGING.name)
        doReturn(
            arrayOf(
                duid,
                Cookie("foo2", "bar2"),
                Cookie("foo3", "bar3"),
            ),
        ).whenever(request).cookies
        doReturn(null).whenever(request).getAttribute(COOKIE_NAME)

        provider.set(ChannelType.SOCIAL, request, response)

        verify(request).setAttribute(COOKIE_NAME, ChannelType.SOCIAL.name)
        assertEquals(ChannelType.SOCIAL.name, duid.value)
    }
}
