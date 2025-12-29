package com.wutsi.koki.platform.ai.llm.gemini.model

import com.wutsi.koki.platform.ai.llm.LLMTool
import com.wutsi.koki.platform.ai.llm.LLMToolConfig

data class GGenerateContentRequest(
    val contents: List<GContent>,
    val generationConfig: GGenerationConfig? = null,
    val tools: List<LLMTool>? = null,
    val toolConfig: LLMToolConfig? = null,
)
