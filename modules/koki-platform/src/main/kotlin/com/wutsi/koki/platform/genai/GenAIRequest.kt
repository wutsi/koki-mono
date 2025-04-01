package com.wutsi.koki.platform.ai.genai

import com.wutsi.koki.platform.ai.genai.gemini.Config
import com.wutsi.koki.platform.genai.Tool

class GenAIRequest(
    val messages: List<Message>,
    val config: Config? = null,
    val tools: List<Tool>? = null
)
