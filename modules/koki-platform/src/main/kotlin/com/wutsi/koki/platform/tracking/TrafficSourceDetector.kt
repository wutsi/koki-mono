package com.wutsi.koki.platform.tracking

import java.net.URL
import java.net.URLDecoder

class TrafficSourceDetector {
    fun detect(url: String?, referer: String?, ua: String?): String? {
        url?.let {
            val source = extractParams(url)["utm_source"]
            if (!source.isNullOrEmpty()) {
                return source
            }
        }

        return if (ua?.contains("GoogleImageProxy", ignoreCase = true) == true) {
            "gmail"
        } else if (ua?.contains("YahooMailProxy", ignoreCase = true) == true) {
            "yahoomail"
        } else if ( // Test before Facebook
            ua?.contains("fban/messenger", ignoreCase = true) == true ||
            ua?.contains("fb_iab/messenger", ignoreCase = true) == true
        ) {
            "messenger"
        } else if ( // Test before Facebook
            ua?.contains("instagram", ignoreCase = true) == true
        ) {
            "instagram"
        } else if (
            ua?.contains("fbav/", ignoreCase = true) == true ||
            referer?.contains("facebook.com", ignoreCase = true) == true
        ) {
            "facebook"
        } else if (ua?.contains("telegrambot (like twitterbot)", ignoreCase = true) == true) { // Test before Twitter
            "telegram"
        } else if (
            ua?.contains("twitter", ignoreCase = true) == true ||
            referer?.endsWith("t.co", ignoreCase = true) == true ||
            referer?.contains("twitter.com", ignoreCase = true) == true
        ) {
            "twitter"
        } else if (
            ua?.contains("whatsapp", ignoreCase = true) == true ||
            referer?.contains("wa.me", ignoreCase = true) == true ||
            referer?.contains("whatsapp.com", ignoreCase = true) == true
        ) {
            "whatsapp"
        } else if (referer?.contains("reddit.com", ignoreCase = true) == true) {
            "reddit"
        } else if (referer?.contains("linkedin.com", ignoreCase = true) == true) {
            "linkedin"
        } else {
            null
        }
    }

    private fun extractParams(url: String): Map<String, String?> {
        try {
            val params = LinkedHashMap<String, String>()
            val query = URL(url).query
            val pairs = query.split("&".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            for (pair in pairs) {
                val idx = pair.indexOf("=")
                val name = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                params[name] = value
            }
            return params
        } catch (ex: Exception) {
            return emptyMap()
        }
    }
}
