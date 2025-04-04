package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DSChoice(
    @get:JsonProperty("finish_reason")
    val finishReason: String = "",

    val index: Int = -1,
    val message: DSChoiceMessage = DSChoiceMessage(),
)
