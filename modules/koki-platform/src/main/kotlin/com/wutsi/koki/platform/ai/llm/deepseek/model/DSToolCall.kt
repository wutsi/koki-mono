package com.wutsi.koki.platform.ai.llm.deepseek.model

data class DSToolCall(
    val id: String = "",
    val type: String = "function",
    val function: DSFunctionCall = DSFunctionCall()
)
