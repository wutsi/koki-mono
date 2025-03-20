package com.wutsi.koki.platform.ai.genai.gemini.model

data class GGenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val topK: Double? = null,
    val maxOutputTokens: Int? = null,
)
