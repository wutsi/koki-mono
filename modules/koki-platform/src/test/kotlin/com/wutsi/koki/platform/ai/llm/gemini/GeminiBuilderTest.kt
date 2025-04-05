package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.LLMNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GeminiBuilderTest {
    val config = mapOf(
        ConfigurationName.AI_PROVIDER_GEMINI_MODEL to "model-xyz",
        ConfigurationName.AI_PROVIDER_GEMINI_API_KEY to "sk-43904309340"
    )

    val builder = GeminiBuilder(1000, 1000)

    @Test
    fun build() {
        val service = builder.build(config)
        assertTrue(service is Gemini)
    }

    @Test
    fun missingModel() {
        assertThrows<LLMNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_PROVIDER_GEMINI_MODEL })
        }
    }

    @Test
    fun missingApiKey() {
        assertThrows<LLMNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_PROVIDER_GEMINI_API_KEY })
        }
    }
}
