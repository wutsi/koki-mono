package com.wutsi.koki.platform.ai.genai.koki

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class KokiBuilderTest {
    val delegate = mock<GeminiBuilder>()
    val builder = KokiBuilder(
        apiKey = "1111",
        model = "xxx",
        delegate = delegate
    )

    @Test
    fun build() {
        val gemini = mock<LLM>()
        doReturn(gemini).whenever(delegate).build(any())

        val result = builder.build()
        assertEquals(gemini, result)

        val arg = argumentCaptor<Map<String, String>>()
        verify(delegate).build(arg.capture())
        assertEquals("1111", arg.firstValue[ConfigurationName.AI_MODEL_GEMINI_API_KEY])
        assertEquals("xxx", arg.firstValue[ConfigurationName.AI_MODEL_GEMINI_MODEL])
    }
}
