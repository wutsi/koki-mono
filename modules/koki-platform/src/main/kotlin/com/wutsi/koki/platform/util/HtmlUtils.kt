package com.wutsi.koki.platform.util

object HtmlUtils {
    fun toHtml(str: String?): String {
        return str?.replace("\n", "<br>") ?: ""
    }
}
