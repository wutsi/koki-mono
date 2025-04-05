package com.wutsi.koki.platform.ai.genai.koki

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals

class KokiBuilderTest {
    val gemini = mock<GeminiBuilder>()
    val deepseek = mock<DeepseekBuilder>()

    @Test
    fun deepseek() {
        val builder = createBuilder(LLMType.DEEPSEEK)

        val llm = mock<LLM>()
        doReturn(llm).whenever(deepseek).build(any())

        val result = builder.build()
        assertEquals(llm, result)

        val arg = argumentCaptor<Map<String, String>>()
        verify(deepseek).build(arg.capture())
        assertEquals("2222", arg.firstValue[ConfigurationName.AI_PROVIDER_DEEPSEEK_API_KEY])
        assertEquals("yyy", arg.firstValue[ConfigurationName.AI_PROVIDER_DEEPSEEK_MODEL])
    }

    @Test
    fun gemini() {
        val builder = createBuilder(LLMType.GEMINI)

        val llm = mock<LLM>()
        doReturn(llm).whenever(gemini).build(any())

        val result = builder.build()
        assertEquals(llm, result)

        val arg = argumentCaptor<Map<String, String>>()
        verify(gemini).build(arg.capture())
        assertEquals("1111", arg.firstValue[ConfigurationName.AI_PROVIDER_GEMINI_API_KEY])
        assertEquals("xxx", arg.firstValue[ConfigurationName.AI_PROVIDER_GEMINI_MODEL])
    }

    @Test
    fun unsupported() {
        val builder = createBuilder(LLMType.KOKI)
        assertThrows<IllegalStateException> { builder.build() }
    }

    private fun createBuilder(type: LLMType): KokiBuilder {
        return KokiBuilder(
            type = type,

            geminiApiKey = "1111",
            geminiModel = "xxx",
            geminiBuilder = gemini,

            deepseekApiKey = "2222",
            deepseekModel = "yyy",
            deepseekBuilder = deepseek,
        )
    }
}
