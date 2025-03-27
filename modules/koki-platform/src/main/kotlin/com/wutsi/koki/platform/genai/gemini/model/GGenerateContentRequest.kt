package com.wutsi.koki.platform.ai.genai.gemini.model

data class GGenerateContentRequest(
    val contents: List<GContent> = emptyList(),
    val generationConfig: GGenerationConfig? = null
)
