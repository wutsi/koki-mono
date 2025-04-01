package com.wutsi.koki.platform.ai.llm.koki

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName

class KokiBuilder(
    private val apiKey: String,
    private val model: String,
    private val delegate: GeminiBuilder,
) {
    fun build(): LLM {
        return delegate.build(
            mapOf(
                ConfigurationName.AI_MODEL_GEMINI_MODEL to model,
                ConfigurationName.AI_MODEL_GEMINI_API_KEY to apiKey,
            )
        )
    }
}
