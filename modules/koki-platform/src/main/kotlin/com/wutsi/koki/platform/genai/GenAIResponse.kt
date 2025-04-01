package com.wutsi.koki.platform.ai.genai

import com.wutsi.koki.platform.genai.Usage

class GenAIResponse(
    val messages: List<Message> = emptyList(),
    val usage: Usage? = null,
)
