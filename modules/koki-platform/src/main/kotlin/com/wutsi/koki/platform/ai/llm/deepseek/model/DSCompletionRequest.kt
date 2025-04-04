package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class DSCompletionRequest(
    val model: String,
    val messages: List<DSMessage>,
    val tools: List<DSTool>? = null,
    val stream: Boolean = false,
    val temperature: Double? = null,

    @get:JsonProperty("response_format")
    val responseFormat: Map<String, String>? = null,

    @get:JsonProperty("top_p")
    val topP: Double? = null,
)
