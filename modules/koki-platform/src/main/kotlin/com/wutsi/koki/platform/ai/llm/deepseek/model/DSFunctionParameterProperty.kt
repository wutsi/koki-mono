package com.wutsi.koki.platform.ai.llm.deepseek

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DSFunctionParameterProperty(
    val type: String,
    val description: String,
    val enum: List<String>? = null,
)
