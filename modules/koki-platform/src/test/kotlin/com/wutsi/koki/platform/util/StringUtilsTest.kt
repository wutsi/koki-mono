package com.wutsi.koki.platform.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun toSlug() {
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a Slug"))
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This-is a Slug"))
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This-is a ,Slug"))
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This.is!a,Slug"))
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a Slug?"))
        assertEquals("/read/123/this-is-a-slug", StringUtils.toSlug("/read/123", "This is a\nSlug?"))
    }

    @Test
    fun whatsapp() {
        assertEquals("https://wa.me/15147580102", StringUtils.toWhatsappUrl("+1514 758-01-02"))
        assertEquals("https://wa.me/15147580102?text=hello", StringUtils.toWhatsappUrl("+1514 758-01-02", "hello"))
        assertEquals(
            "https://wa.me/15147580102?text=I%27m+interested+in+your+car+for+sale",
            StringUtils.toWhatsappUrl("+1514 758-01-02", "I'm interested in your car for sale")
        )
    }
}
