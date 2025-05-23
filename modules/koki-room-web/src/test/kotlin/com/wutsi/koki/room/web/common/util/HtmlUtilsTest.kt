package com.wutsi.koki.room.web.common.util

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlUtilsTest {
    @Test
    fun cr() {
        assertEquals("Yo<br>Man", HtmlUtils.toHtml("Yo\nMan"))
    }
}
