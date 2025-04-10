package com.wutsi.koki.platform.ai.llm

data class Tool(
    val functionDeclarations: List<FunctionDeclaration> = emptyList(),
)
