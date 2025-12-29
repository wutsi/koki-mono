package com.wutsi.koki.platform.ai.llm

data class LLMFunctionParameterProperty(
    val type: LLMType = LLMType.STRING,
    val description: String,
    val enum: List<String>? = null,
)
