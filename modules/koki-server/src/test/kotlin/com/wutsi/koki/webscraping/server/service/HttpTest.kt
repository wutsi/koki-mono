package com.wutsi.koki.webscraping.server.service

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class HttpTest {
    private val http = Http()

    @Test
    fun hash() {
        val url = "https://www.wutsi.com"
        val hash = http.hash(url)

        assertEquals("b32e5c02a626c9505a0f6ad797b92a3f", hash)
    }

    @Test
    fun get() {
        val html = http.get("https://www.cnn.com")
        assert(html.contains("<html"))
    }
}
