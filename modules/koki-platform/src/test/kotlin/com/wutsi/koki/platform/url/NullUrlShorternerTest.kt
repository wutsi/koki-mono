package com.wutsi.koki.platform.url

import kotlin.test.Test
import kotlin.test.assertEquals

class NullUrlShorternerTest {
    val shortener = NullUrlShortener()

    @Test
    fun shorten() {
        val url = "https://www.google.ca"
        assertEquals(url, shortener.shorten(url))
    }
}
