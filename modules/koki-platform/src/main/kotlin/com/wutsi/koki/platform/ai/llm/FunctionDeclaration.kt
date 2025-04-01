package com.wutsi.koki.platform.ai.llm

data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: FunctionParameters,
)
