package com.wutsi.koki.platform.translation.ai

import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.platform.translation.TranslationService

class AITranslationBuilder(private val delegate: LLMBuilder) {
    fun build(config: Map<String, String>): TranslationService {
        return AITranslationService(
            llm = delegate.build(config)
        )
    }
}
