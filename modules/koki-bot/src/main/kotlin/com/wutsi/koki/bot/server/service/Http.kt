package com.wutsi.koki.bot.server.service

import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class Http {
    companion object {
        const val USER_AGENT =
            "kokibot/5.0"
    }

    fun html(url: String): String {
        return Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .followRedirects(true)
            .get()
            .html()
    }

    fun hash(value: String): String {
        val xvalue = value.trimEnd('/').lowercase().trim()
        return DigestUtils.md5Hex(xvalue)
    }
}
