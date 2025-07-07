package com.wutsi.koki.platform.tracking.servlet

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.tracking.ChannelTypeDetector
import com.wutsi.koki.platform.tracking.ChannelTypeProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

class ChannelTypeFilter(
    private val detector: ChannelTypeDetector,
    private val logger: KVLogger,
    private val provider: ChannelTypeProvider,
    private val serverUrl: String,
    private val ignoreURIPrefixes: List<String> = listOf(
        "/.well-known/",
        "/error/",
        "/js/",
        "/css/",
        "/image/",
        "/manifest.json",
        "/service-worker.js"
    )
) : OncePerRequestFilter() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ChannelTypeFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            if (!accept(request)) {
                return
            }

            val ua = request.getHeader("User-Agent")
            val url = (request.requestURL?.toString() ?: "") + "?" + (request.queryString ?: "")
            val referer: String? = request.getHeader("Referer")
            val channelType = detector.detect(url, referer ?: "", ua)
            logger.add("http_channel", channelType)
            provider.set(channelType, request, response)
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        } finally {
            filterChain.doFilter(request, response)
        }
    }

    private fun isExternal(referer: String?): Boolean {
        return referer.isNullOrEmpty() || extractDomain(referer) != extractDomain(serverUrl)
    }

    private fun extractDomain(url: String): String {
        var domainName: String = url
        var index: Int = url.indexOf("://")
        if (index != -1) {
            // keep everything after the "://"
            domainName = domainName.substring(index + 3)
        }

        index = domainName.indexOf('/')
        if (index != -1) {
            // keep everything before the '/'
            domainName = domainName.substring(0, index)
        }

        return domainName.replaceFirst("www.", "")
    }

    private fun accept(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        if (uri != null && ignoreURIPrefixes.find { prefix -> uri.startsWith(prefix) } != null) {
            return false
        } else {
            val referer: String? = request.getHeader("Referer")
            return isExternal(referer)
        }
    }
}
