package com.wutsi.koki.webscraping.server.service

import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Http {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebscraperService::class.java)
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
    }

    fun get(url: String): String {
        return Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .followRedirects(true)
            .get()
            .html()
    }

    fun hash(value: String): String {
        val xvalue = if (value.endsWith("/")) {
            value.substring(0, value.length - 1)
        } else {
            value
        }
        return DigestUtils.md5Hex(xvalue.lowercase().trim())
    }
}
