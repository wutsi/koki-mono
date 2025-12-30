package com.wutsi.koki.platform.util.html

interface HtmlFilter {
    fun filter(html: String): String
}
