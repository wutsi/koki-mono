package com.wutsi.koki.platform.ai.llm.deepseek

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DSFunction(
    val name: String,
    val description: String,
    val parameters: DSFunctionParameters? = null,
)
