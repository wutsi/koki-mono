package com.wutsi.koki.portal.common

object HtmlUtils {
    fun toHtml(str: String): String {
        return str.replace("\n", "<br>")
    }
}
