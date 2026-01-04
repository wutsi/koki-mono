package com.wutsi.koki.platform.ai.llm.deepseek

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLMType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertContains

class DeepseekWebsearchToolTest {
    private val websearch = mock(Websearch::class.java)
    private val tool = DeepseekWebsearchTool(websearch)

    @Test
    fun function() {
        // When
        val function = tool.function()

        // Then
        assertEquals("deepseek_websearch", function.name)
        assertNotNull(function.description)

        val parameters = function.parameters
        assertNotNull(parameters)
        assertEquals(LLMType.OBJECT, parameters?.type)
        assertEquals(1, parameters?.properties?.size)
        assertEquals(true, parameters?.properties?.containsKey("q"))

        val qParam = function.parameters?.properties?.get("q")
        assertNotNull(qParam)
        assertEquals(LLMType.STRING, qParam?.type)
        assertNotNull(qParam?.description)

        val required = function.parameters?.required
        assertNotNull(required)
        assertEquals(1, required?.size)
        assertContains(required!!, "q")
    }

    @Test
    fun use() {
        // Given
        val query = "What is the weather in Paris?"
        val expectedResult = "Result #1: Weather in Paris is sunny\nResult #2: Temperature is 25C"
        val args = mapOf("q" to query)
        doReturn(expectedResult).whenever(websearch).search(query)

        // When
        val result = tool.use(args)

        // Then
        println(result)
        verify(websearch).search(query)
        assertEquals(true, result.contains(expectedResult))
    }
}
