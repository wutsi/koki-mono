package com.wutsi.koki.room.web.common

object HtmlUtils {
    fun toHtml(str: String): String {
        return str.replace("\n", "<br>")
    }
}
