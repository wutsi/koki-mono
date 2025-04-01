package com.wutsi.koki.platform.ai.llm

data class Message(
    val role: Role = Role.USER,
    val text: String? = null,
    val document: Document? = null,
    val functionCall: FunctionCall? = null,
)
