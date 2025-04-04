package com.wutsi.koki.platform.ai.llm

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class LLMBuilderTest {
    private val geminiBuilder = mock<GeminiBuilder>()
    private val kokiBuilder = mock<KokiBuilder>()
    private val deepseekBuilder = mock<DeepseekBuilder>()
    private val builder = LLMBuilder(
        gemini = geminiBuilder,
        koki = kokiBuilder,
        deepseek = deepseekBuilder,
    )

    @Test
    fun gemini() {
        val service = mock<LLM>()
        doReturn(service).whenever(geminiBuilder).build(any())

        val config = mapOf(ConfigurationName.AI_MODEL to LLMType.GEMINI.name)
        assertEquals(service, builder.build(config))
    }

    @Test
    fun deepseek() {
        val service = mock<LLM>()
        doReturn(service).whenever(deepseekBuilder).build(any())

        val config = mapOf(ConfigurationName.AI_MODEL to LLMType.DEEPSEEK.name)
        assertEquals(service, builder.build(config))
    }

    @Test
    fun koki() {
        val service = mock<LLM>()
        doReturn(service).whenever(kokiBuilder).build()

        val config = mapOf(ConfigurationName.AI_MODEL to LLMType.KOKI.name)
        assertEquals(service, builder.build(config))
    }

    @Test
    fun error() {
        val service = mock<LLM>()
        doReturn(service).whenever(kokiBuilder).build()

        val config = mapOf(ConfigurationName.AI_MODEL to "xxx")
        assertThrows<LLMNotConfiguredException> { builder.build(config) }
    }
}
