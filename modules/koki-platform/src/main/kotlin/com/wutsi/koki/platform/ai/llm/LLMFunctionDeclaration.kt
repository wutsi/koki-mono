package com.wutsi.koki.platform.ai.llm

data class LLMFunctionDeclaration(
    val builtIn: Boolean = false,
    val name: String,
    val description: String,
    val parameters: LLMFunctionParameters? = null,
)
