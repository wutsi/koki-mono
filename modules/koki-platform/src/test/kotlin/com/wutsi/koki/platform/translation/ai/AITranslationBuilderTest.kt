package com.wutsi.koki.platform.translation.ai

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMBuilder
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AITranslationBuilderTest {
    private val configs = emptyMap<String, String>()
    private val ai = mock<LLMBuilder>()
    private val builder = AITranslationBuilder(ai)

    @Test
    fun translate() {
        val llm = mock<LLM>()
        doReturn(llm).whenever(ai).build(any())

        val service = builder.build(configs)

        assertEquals(true, service is AITranslationService)
    }
}
