package com.wutsi.koki.email.server.service.filter

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlEscapeFilterTest {
    val filter = HtmlEscapeFilter()

    @Test
    fun filter() {
        val result = filter.filter("Voilà du <b>français</b>", 111L)
        assertEquals("Voil&agrave; du <b>fran&ccedil;ais</b>", result)
    }
}
