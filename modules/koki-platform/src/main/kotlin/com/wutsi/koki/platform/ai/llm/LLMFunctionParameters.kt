package com.wutsi.koki.platform.ai.llm

data class LLMFunctionParameters(
    val type: LLMType = LLMType.OBJECT,
    val properties: Map<String, LLMFunctionParameterProperty> = emptyMap(),
    val required: List<String> = emptyList(),
)
