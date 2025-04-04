package com.wutsi.koki.platform.translation.ai

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.translation.TranslationException
import com.wutsi.koki.platform.translation.TranslationService
import java.util.Locale

class LLMTranslationBuilder(
    private val delegate: LLMBuilder
) {
    fun build(config: Map<String, String>): TranslationService{
        
    }
}
