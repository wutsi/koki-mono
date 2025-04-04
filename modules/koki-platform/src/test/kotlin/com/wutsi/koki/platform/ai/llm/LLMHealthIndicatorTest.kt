package com.wutsi.koki.platform.ai.llm

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.mock
import org.springframework.boot.actuate.health.Status
import java.lang.RuntimeException
import kotlin.test.Test
import kotlin.test.assertEquals

class LLMHealthIndicatorTest {
    private val llm = mock<LLM>()
    private val health = LLMHealthIndicator(llm)

    @Test
    fun up() {
        doReturn(LLMResponse()).whenever(llm).generateContent(any())

        val result = health.health()

        assertEquals(Status.UP, result.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException("yo")).whenever(llm).generateContent(any())

        val result = health.health()

        assertEquals(Status.DOWN, result.status)
    }
}
