package com.wutsi.koki.platform.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberUtilsTest {
    @Test
    fun `short test`() {
        assertEquals("", NumberUtils.shortText(0))
        assertEquals("999", NumberUtils.shortText(999))
        assertEquals("1K", NumberUtils.shortText(1000))
        assertEquals("1Mb", NumberUtils.shortText(999950, "b"))
        assertEquals("1Mb", NumberUtils.shortText(1000000, "b"))
        assertEquals("1.5M", NumberUtils.shortText(1500000))
        assertEquals("1G", NumberUtils.shortText(1000000000))
    }
}
