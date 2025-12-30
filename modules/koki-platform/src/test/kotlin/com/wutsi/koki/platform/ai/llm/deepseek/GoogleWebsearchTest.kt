package com.wutsi.koki.platform.ai.llm.deepseek

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class GoogleWebsearchTest {
    @Test
    fun search() {
        val tool = DuckDuckGoWebsearch()
        val results = tool.search("Whats the temperature in Montreal?")
        println(results)
        assertTrue(results.contains("Result #1"))
        assertTrue(results.contains("Result #2"))
        assertTrue(results.contains("Result #3"))
        assertTrue(results.contains("Result #4"))
        assertTrue(results.contains("Result #5"))
        assertTrue(results.contains("Result #6"))
        assertTrue(results.contains("Result #7"))
        assertTrue(results.contains("Result #8"))
        assertTrue(results.contains("Result #9"))
        assertTrue(results.contains("Result #10"))
    }
}
