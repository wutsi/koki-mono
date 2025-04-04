package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DSChoiceMessage(
    val role: String = "",
    val content: String? = null,

    @get:JsonProperty("reasoning_content")
    val reasoningContent: String? = null,

    @get:JsonProperty("tool_calls")
    val toolCalls: List<DSToolCall> = emptyList()
)
