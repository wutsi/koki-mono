package com.wutsi.koki.platform.ai.llm

class LLMRequest(
    val messages: List<Message>,
    val config: Config? = null,
    val tools: List<Tool>? = null
)
