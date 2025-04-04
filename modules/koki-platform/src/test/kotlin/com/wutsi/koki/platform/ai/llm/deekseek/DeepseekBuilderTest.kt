package com.wutsi.koki.platform.ai.llm.deekseek

import com.wutsi.koki.platform.ai.llm.LLMNotConfiguredException
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class DeepseekBuilderTest {
    val config = mapOf(
        ConfigurationName.AI_MODEL_DEEPSEEK_MODEL to "model-xyz",
        ConfigurationName.AI_MODEL_DEEPSEEK_API_KEY to "sk-43904309340"
    )

    val builder = DeepseekBuilder(1000, 1000)

    @Test
    fun build() {
        val service = builder.build(config)
        assertTrue(service is Gemini)
    }

    @Test
    fun missingModel() {
        assertThrows<LLMNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_MODEL_DEEPSEEK_MODEL })
        }
    }

    @Test
    fun missingApiKey() {
        assertThrows<LLMNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_MODEL_DEEPSEEK_API_KEY })
        }
    }
}
