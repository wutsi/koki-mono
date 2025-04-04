package com.wutsi.koki.platform.ai.llm.deepseek.model

data class DSFunctionCall(
    val name: String = "",
    val arguments: String = "",
)
