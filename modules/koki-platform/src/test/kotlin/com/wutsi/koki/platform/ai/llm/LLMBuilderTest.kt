package com.wutsi.koki.platform.ai.llm

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class LLMBuilderTest {
    private val geminiBuilder = mock<GeminiBuilder>()
    private val kokiBuilder = mock<KokiBuilder>()
    private val deepseekBuilder = mock<DeepseekBuilder>()
    private val config = mapOf("x" to "xxx")
    private val builder = LLMBuilder(
        gemini = geminiBuilder,
        koki = kokiBuilder,
        deepseek = deepseekBuilder,
    )

    @Test
    fun gemini() {
        val service = mock<LLM>()
        doReturn(service).whenever(geminiBuilder).build(any())

        assertEquals(service, builder.build(LLMType.GEMINI, config))
    }

    @Test
    fun deepseek() {
        val service = mock<LLM>()
        doReturn(service).whenever(deepseekBuilder).build(any())

        assertEquals(service, builder.build(LLMType.DEEPSEEK, config))
    }

    @Test
    fun koki() {
        val service = mock<LLM>()
        doReturn(service).whenever(kokiBuilder).build()

        assertEquals(service, builder.build(LLMType.KOKI, config))
    }
}
