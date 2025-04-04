package com.wutsi.koki.platform.ai.llm.deepseek

data class DSFunctionParameters(
    val type: String,
    val properties: Map<String, DSFunctionParameterProperty>,
    val required: List<String> = emptyList(),
)
