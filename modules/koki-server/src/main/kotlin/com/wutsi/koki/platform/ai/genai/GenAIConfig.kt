package com.wutsi.koki.platform.ai.genai.gemini

import org.springframework.http.MediaType

data class GenAIConfig(
    val responseType: MediaType? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
    val topK: Double? = null,
    val maxOutputTokens: Int? = null,
)
