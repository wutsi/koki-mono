package com.wutsi.koki.platform.ai.llm

data class FunctionParameterProperty(
    val type: Type = Type.STRING,
    val description: String,
    val enum: List<String>? = null,
)
