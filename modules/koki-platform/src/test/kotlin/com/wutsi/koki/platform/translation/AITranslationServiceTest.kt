package com.wutsi.koki.platform.translation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AITranslationServiceTest {
    private val llm = mock<LLM>()
    private val service = AITranslationService(llm)

    @Test
    fun translate() {
        val translation = "Bonjour le monde"
        doReturn(
            LLMResponse(
                messages = listOf(Message(role = Role.MODEL, text = translation))
            )
        ).whenever(llm).generateContent(any())

        val result = service.translate("Hello world", "en")
        assertEquals(translation, result)

        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())

        val messages = request.firstValue.messages
        assertEquals(Role.SYSTEM, messages[0].role)
        assertEquals(AITranslationService.SYSTEM_INSTRUCTIONS.trimIndent(), messages[0].text)

        assertEquals(Role.USER, messages[1].role)
        assertEquals(
            """
                Can you translate the following text to English:
                Hello world
            """.trimIndent(),
            messages[1].text
        )
    }

    @Test
    fun `no transaction`() {
        doReturn(LLMResponse()).whenever(llm).generateContent(any())

        val result = service.translate("Hello world", "en")
        assertEquals(null, result)
    }

    @Test
    fun `empty transaction`() {
        doReturn(LLMResponse(messages = listOf(Message(text = "")))).whenever(llm).generateContent(any())

        val result = service.translate("Hello world", "en")
        assertEquals(null, result)
    }
}
