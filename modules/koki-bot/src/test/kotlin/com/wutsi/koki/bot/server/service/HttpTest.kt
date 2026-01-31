package com.wutsi.koki.bot.server.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class HttpTest {
    private val http = Http()

    @Test
    fun html() {
        val result = http.html("https://www.google.com")
        assertTrue(result.lowercase().contains("<html"))
    }

    @Test
    fun hash() {
        assertEquals("8ffdefbdec956b595d257f0aaeefd623", http.hash("https://www.google.com"))
    }

    @Test
    fun `hash - without trailing slash`() {
        assertEquals("8ffdefbdec956b595d257f0aaeefd623", http.hash("https://www.google.com/"))
    }

    @Test
    fun `hash - ignore case`() {
        assertEquals("8ffdefbdec956b595d257f0aaeefd623", http.hash("https://WWW.google.COm"))
    }
}
