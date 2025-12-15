package com.wutsi.koki.platform.ai.llm

data class Content(
    val text: String? = null,
    val document: Document? = null,
    val functionCall: FunctionCall? = null,
)
