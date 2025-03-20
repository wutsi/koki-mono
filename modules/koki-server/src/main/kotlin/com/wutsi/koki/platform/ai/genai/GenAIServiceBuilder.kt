package com.wutsi.koki.platform.ai.genai

import com.wutsi.koki.platform.ai.genai.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.genai.koki.KokiBuilder

class GenAIServiceBuilder(
    private val gemini: GeminiBuilder,
    private val koki: KokiBuilder,
) {
    fun build(type: GenAIType, config: Map<String, String>): GenAIService {
        return when (type) {
            GenAIType.KOKI -> koki.build()
            GenAIType.GEMINI -> gemini.build(config)
        }
    }
}
