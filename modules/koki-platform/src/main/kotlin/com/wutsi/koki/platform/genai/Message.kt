package com.wutsi.koki.platform.ai.genai

import com.wutsi.koki.platform.genai.FunctionCall

data class Message(
    val role: Role = Role.USER,
    val text: String? = null,
    val document: Document? = null,
    val functionCall: FunctionCall? = null,
)
