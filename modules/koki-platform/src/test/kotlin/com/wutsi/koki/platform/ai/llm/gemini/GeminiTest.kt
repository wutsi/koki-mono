package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.LLM
import kotlin.test.Test

class GeminiTest { // AbstractLLMTest()
    @Test
    fun nothing() {
        // Nothing
    }

    fun createLLM(): LLM {
        return Gemini(
            apiKey = System.getenv("GEMINI_API_KEY"),
            model = "gemini-2.5-flash-lite",
        )
    }

    fun createVisionLLM(): LLM {
        return createLLM()
    }
}
