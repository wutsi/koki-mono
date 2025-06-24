package com.wutsi.koki.platform.url

interface UrlShortener {
    fun shorten(url: String): String
}
