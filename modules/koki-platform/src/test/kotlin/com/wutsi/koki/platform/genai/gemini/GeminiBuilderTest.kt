package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.GenAINotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GeminiBuilderTest {
    val config = mapOf(
        ConfigurationName.AI_MODEL_GEMINI_MODEL to "model-xyz",
        ConfigurationName.AI_MODEL_GEMINI_API_KEY to "sk-43904309340"
    )

    val builder = GeminiBuilder(1000, 1000)

    @Test
    fun build() {
        val service = builder.build(config)
        assertTrue(service is Gemini)
    }

    @Test
    fun missingModel() {
        assertThrows<GenAINotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_MODEL_GEMINI_MODEL })
        }
    }

    @Test
    fun missingApiKey() {
        assertThrows<GenAINotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.AI_MODEL_GEMINI_API_KEY })
        }
    }
}
