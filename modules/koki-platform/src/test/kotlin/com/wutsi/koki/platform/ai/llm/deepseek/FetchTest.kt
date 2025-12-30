package com.wutsi.koki.platform.ai.llm.deepseek

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue

class FetchTest {
    @Test
    fun test() {
        val url = "https://www.cnn.com/2025/12/29/politics/cia-drone-strike-venezuela"
        val tools = Fetch()
        val content = tools.fetch(url)
        println(content)

        assertTrue(content.contains("CNN"))
    }

    @Test
    fun `connect error`() {
        val url = "https://www.reoirxxfdkfdlk.com"
        val tools = Fetch()
        val content = tools.fetch(url)

        assertEquals("Failed to connect to $url", content)
    }

    @Test
    fun `404 error`() {
        val url = "https://www.cnn.com/fdlfdlk-this-path-does-not-exist"
        val tools = Fetch()
        val content = tools.fetch(url)

        assertEquals("Failed to get the content from $url - The page doesn't exist or is inaccessible", content)
    }

    @Test
    fun image() {
        val url = "https://picsum.photos/800/600"
        val tools = Fetch()
        val content = tools.fetch(url)

        assertEquals(true, content.contains("Failed to get the content from $url"))
    }
}
