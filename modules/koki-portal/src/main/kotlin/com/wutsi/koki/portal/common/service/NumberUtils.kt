package com.wutsi.koki.portal.common.service

import java.text.DecimalFormat
import java.text.StringCharacterIterator

object NumberUtils {
    fun shortText(value: Long, unit: String? = "", format: String = "#.#"): String {
        val fmt = DecimalFormat(format)
        var bytes = value
        if (bytes == 0L) {
            return ""
        } else if (-1000 < bytes && bytes < 1000) {
            return bytes.toString()
        }
        val ci = StringCharacterIterator("KMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        return fmt.format(bytes / 1000.0) + ci.current() + unit
    }
}
