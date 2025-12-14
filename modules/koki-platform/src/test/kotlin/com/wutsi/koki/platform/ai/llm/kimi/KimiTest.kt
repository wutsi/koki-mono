package com.wutsi.koki.platform.ai.llm.kimi

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM

class KimiTest : AbstractLLMTest() {
    override fun createVisionLLM(): LLM {
        return Kimi(
            apiKey = System.getenv("KIMI_API_KEY"),
            model = "moonshot-v1-8k-vision-preview"
        )
    }

    override fun createLLM(): LLM {
        return Kimi(
            apiKey = System.getenv("KIMI_API_KEY"),
            model = "kimi-k2-turbo-preview",
        )
    }
}
