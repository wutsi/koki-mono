package com.wutsi.koki.platform.ai.genai

import com.wutsi.koki.platform.ai.genai.gemini.GenAIConfig

class GenAIRequest(
    val messages: List<Message>,
    val config: GenAIConfig? = null
)
