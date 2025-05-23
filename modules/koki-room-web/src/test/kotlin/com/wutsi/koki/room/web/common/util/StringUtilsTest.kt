package com.wutsi.koki.room.web.common.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun generate() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a Slug"))
    }

    @Test
    fun filterDash() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This-is a Slug"))
    }

    @Test
    fun filterMultipleDash() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This-is a ,Slug"))
    }

    @Test
    fun filterPuctuation() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This.is!a,Slug"))
    }

    @Test
    fun filterTrailingSeparator() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a Slug?"))
    }

    @Test
    fun filterCR() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a\nSlug?"))
    }
}
