package com.wutsi.koki.platform.ai.llm.gemini.model

data class GGenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val topK: Double? = null,
    val maxOutputTokens: Int? = null,
    val thinkingConfig: GThinkingConfig? = null,
)
