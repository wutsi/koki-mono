package com.wutsi.koki.platform.genai

data class FunctionParameterProperty(
    val type: Type = Type.STRING,
    val description: String,
    val enum: List<String>? = null,
)
