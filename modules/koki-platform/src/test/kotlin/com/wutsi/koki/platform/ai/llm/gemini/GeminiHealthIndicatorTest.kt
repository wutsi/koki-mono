package com.wutsi.koki.platform.ai.llm.gemini

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLMResponse
import org.mockito.Mockito.mock
import org.springframework.boot.actuate.health.Status
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiHealthIndicatorTest {
    private val gemini = mock<Gemini>()
    private val health = GeminiHealthIndicator(gemini)

    @Test
    fun up() {
        doReturn(LLMResponse()).whenever(gemini).generateContent(any())

        val result = health.health()

        assertEquals(Status.UP, result.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException("yo")).whenever(gemini).generateContent(any())

        val result = health.health()

        assertEquals(Status.DOWN, result.status)
    }
}
