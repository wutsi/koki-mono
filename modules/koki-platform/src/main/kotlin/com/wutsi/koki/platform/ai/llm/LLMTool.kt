package com.wutsi.koki.platform.ai.llm

data class LLMTool(
    val functionDeclarations: List<LLMFunctionDeclaration> = emptyList(),
)
