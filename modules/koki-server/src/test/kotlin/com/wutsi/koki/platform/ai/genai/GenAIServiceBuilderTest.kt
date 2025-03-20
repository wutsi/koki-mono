package com.wutsi.koki.platform.ai.genai

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.genai.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.genai.koki.KokiBuilder
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class GenAIServiceBuilderTest {
    private val geminiBuilder = mock<GeminiBuilder>()
    private val kokiBuilder = mock<KokiBuilder>()
    private val config = mapOf("x" to "xxx")
    private val builder = GenAIServiceBuilder(
        gemini = geminiBuilder,
        koki = kokiBuilder
    )

    @Test
    fun gemini() {
        val service = mock<GenAIService>()
        doReturn(service).whenever(geminiBuilder).build(any())

        assertEquals(service, builder.build(GenAIType.GEMINI, config))
    }

    @Test
    fun koki() {
        val service = mock<GenAIService>()
        doReturn(service).whenever(kokiBuilder).build()

        assertEquals(service, builder.build(GenAIType.KOKI, config))
    }
}
