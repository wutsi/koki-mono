package com.wutsi.koki.platform.tracking.servlet

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.tracking.ChannelTypeDetector
import com.wutsi.koki.platform.tracking.ChannelTypeProvider
import com.wutsi.koki.track.dto.ChannelType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChannelTypeFilterTest {
    private val request = mock<HttpServletRequest> { }
    private val response = mock<HttpServletResponse> { }
    private val chain = mock<FilterChain> {}
    private val logger = DefaultKVLogger()
    private val detector = mock<ChannelTypeDetector> {}
    private val provider = mock<ChannelTypeProvider> {}
    private val filter = ChannelTypeFilter(detector, logger, provider, "https://www.wutsi.com")

    @BeforeEach
    fun setUp() {
        doReturn("Googlebot/2.1 (+http://www.google.com/bot.html)").whenever(request).getHeader("User-Agent")
        doReturn(StringBuffer("https://www.wutsi.com/read/123")).whenever(request).requestURL
    }

    @Test
    fun nullReferer() {
        doReturn(null).whenever(request).getHeader("Referer")
        doReturn(ChannelType.WEB).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        verify(provider).set(ChannelType.WEB, request, response)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun emptyReferer() {
        doReturn("").whenever(request).getHeader("Referer")
        doReturn(ChannelType.APP).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        verify(provider).set(ChannelType.APP, request, response)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun internal() {
        doReturn("https://www.wutsi.com/1/putin-in-china").whenever(request).getHeader("Referer")

        filter.doFilter(request, response, chain)

        verify(provider, never()).set(any(), any(), any())
        verify(chain).doFilter(request, response)
    }

    @Test
    fun external() {
        doReturn("https://www.facebook.com").whenever(request).getHeader("Referer")
        doReturn(ChannelType.SOCIAL).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        verify(provider).set(ChannelType.SOCIAL, request, response)
        verify(chain).doFilter(request, response)
    }
}
