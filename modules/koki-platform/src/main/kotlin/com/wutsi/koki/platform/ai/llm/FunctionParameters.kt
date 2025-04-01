package com.wutsi.koki.platform.ai.llm

data class FunctionParameters(
    val type: Type = Type.OBJECT,
    val properties: Map<String, FunctionParameterProperty>,
    val required: List<String> = emptyList(),
)
