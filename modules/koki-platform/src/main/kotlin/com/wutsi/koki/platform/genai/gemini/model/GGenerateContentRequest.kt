package com.wutsi.koki.platform.ai.genai.gemini.model

import com.wutsi.koki.platform.genai.Tool

data class GGenerateContentRequest(
    val contents: List<GContent>,
    val generationConfig: GGenerationConfig? = null,
    val tools: List<Tool>? = null,
)
