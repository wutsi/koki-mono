package com.wutsi.koki.platform.ai.llm.gemini.model

import com.wutsi.koki.platform.ai.llm.Tool

data class GGenerateContentRequest(
    val contents: List<GContent>,
    val generationConfig: GGenerationConfig? = null,
    val tools: List<Tool>? = null,
)
