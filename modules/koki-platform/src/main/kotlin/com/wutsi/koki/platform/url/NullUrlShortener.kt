package com.wutsi.koki.platform.url

class NullUrlShortener : UrlShortener {
    override fun shorten(url: String): String {
        return url
    }
}
