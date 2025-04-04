package com.wutsi.koki.platform.ai.llm.koki

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName

class KokiBuilder(
    private val type: LLMType,
    private val geminiApiKey: String,
    private val geminiModel: String,
    private val geminiBuilder: GeminiBuilder,
    private val deepseekApiKey: String,
    private val deepseekModel: String,
    private val deepseekBuilder: DeepseekBuilder,
) {
    fun build(): LLM {
        return when (type) {
            LLMType.GEMINI -> geminiBuilder.build(
                mapOf(
                    ConfigurationName.AI_MODEL_GEMINI_MODEL to geminiModel,
                    ConfigurationName.AI_MODEL_GEMINI_API_KEY to geminiApiKey,
                )
            )

            LLMType.DEEPSEEK -> deepseekBuilder.build(
                mapOf(
                    ConfigurationName.AI_MODEL_DEEPSEEK_MODEL to deepseekModel,
                    ConfigurationName.AI_MODEL_DEEPSEEK_API_KEY to deepseekApiKey,
                )
            )

            else -> throw IllegalStateException("Not supported: $type")
        }
    }
}
