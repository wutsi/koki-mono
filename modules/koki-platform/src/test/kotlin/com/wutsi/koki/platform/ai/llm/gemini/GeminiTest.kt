package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM
import kotlin.test.Test

class GeminiTest : AbstractLLMTest() {
    @Test
    override fun image() {
        super.image()
    }

    override fun createLLM(): LLM {
        return Gemini(
            apiKey = System.getenv("GEMINI_API_KEY"),
            model = "gemini-2.5-flash-lite",
        )
    }

    override fun createVisionLLM(): LLM {
        return createLLM()
    }
}
