package com.wutsi.koki.platform.ai.llm

import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder

class LLMBuilder(
    private val gemini: GeminiBuilder,
    private val koki: KokiBuilder,
) {
    fun build(type: LLMType, config: Map<String, String>): LLM {
        return when (type) {
            LLMType.KOKI -> koki.build()
            LLMType.GEMINI -> gemini.build(config)
        }
    }
}
