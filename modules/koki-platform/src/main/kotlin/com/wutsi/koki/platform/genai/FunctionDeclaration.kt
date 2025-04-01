package com.wutsi.koki.platform.genai

data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: FunctionParameters,
)
