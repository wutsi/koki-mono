package com.wutsi.koki.platform.ai.llm

class LLMRequest(
    val messages: List<LLMMessage>,
    val config: LLMConfig? = null,
    val tools: List<LLMTool>? = null,
    val toolConfig: LLMToolConfig? = null,
)
