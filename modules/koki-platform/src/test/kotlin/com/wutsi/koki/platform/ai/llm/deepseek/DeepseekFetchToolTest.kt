package com.wutsi.koki.platform.ai.llm.deepseek

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLMType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DeepseekFetchToolTest {
    private val fetch = mock(Fetch::class.java)
    private val tool = DeepseekFetchTool(fetch)

    @Test
    fun function() {
        // When
        val function = tool.function()

        // Then
        assertEquals("deepseek_fetch", function.name)
        assertNotNull(function.description)

        val parameters = function.parameters
        assertNotNull(parameters)
        assertEquals(LLMType.OBJECT, parameters?.type)
        assertEquals(1, parameters?.properties?.size)
        assertEquals(true, parameters?.properties?.containsKey("url"))

        val qParam = function.parameters?.properties?.get("url")
        assertNotNull(qParam)
        assertEquals(LLMType.STRING, qParam?.type)
        assertNotNull(qParam?.description)

        val required = function.parameters?.required
        assertNotNull(required)
        assertEquals(1, required?.size)
        assertContains(required!!, "url")
    }

    @Test
    fun use() {
        // Given
        val url = "https://www.example.com"
        val expectedResult = "Result #1: Weather in Paris is sunny\nResult #2: Temperature is 25C"
        val args = mapOf("url" to url)
        doReturn(expectedResult).whenever(fetch).fetch(url)

        // When
        val result = tool.use(args)

        // Then
        verify(fetch).fetch(url)
        assertEquals(true, result.contains(expectedResult))
    }
}
