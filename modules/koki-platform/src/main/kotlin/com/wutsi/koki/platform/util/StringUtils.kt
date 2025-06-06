package com.wutsi.koki.platform.util

import java.net.URLEncoder
import java.text.Normalizer

object StringUtils {
    private val FILTER1 = "\\s+|-+|\\p{Punct}".toRegex()
    private val FILTER2 = "[-*]{2,}".toRegex()
    private val SEPARATOR = "-"

    fun toSlug(prefix: String, name: String?): String {
        var xname = toAscii(name)
            .replace("\n", SEPARATOR)
            .replace(FILTER1, SEPARATOR)
            .replace(FILTER2, SEPARATOR)
            .lowercase()
        if (xname.endsWith(SEPARATOR)) {
            xname = xname.substring(0, xname.length - 1)
        }
        if (xname.startsWith(SEPARATOR)) {
            xname = xname.substring(1)
        }

        return if (xname.length > 0) "$prefix/$xname" else prefix
    }

    fun toAscii(string: String?): String {
        if (string == null || string.isEmpty()) {
            return ""
        }

        var str = string.trim()
        val sb = StringBuilder(str.length)
        str = Normalizer.normalize(str, Normalizer.Form.NFD)
        for (c in str.toCharArray()) {
            if (c <= '\u007F') sb.append(c)
        }
        return sb.toString()
    }

    fun toWhatsappUrl(phone: String, text: String? = null): String {
        val url = "https://wa.me/" +
            phone.replace("+", "")
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "")
        return if (text.isNullOrEmpty()) {
            url
        } else {
            "$url?text=" + URLEncoder.encode(text, "utf-8")
        }
    }
}
