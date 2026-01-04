package com.wutsi.koki.platform.ai.llm.deepseek

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class DuckDuckGoWebsearchTest {
    @Test
    fun search() {
        val tool = DuckDuckGoWebsearch(minDelayMillis = 10, maxDelayMillis = 50, maxResults = 5)
        val results = tool.search("Bastos neighborhood Yaoundé Cameroon description demographics amenities security")
        println(results)
        assertTrue(results.contains("Result #1"))
        assertTrue(results.contains("Result #2"))
        assertTrue(results.contains("Result #3"))
        assertTrue(results.contains("Result #4"))
        assertTrue(results.contains("Result #5"))
    }
}
