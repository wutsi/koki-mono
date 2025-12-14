package com.wutsi.koki.platform.ai.llm.kimi

import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek

class Kimi(
    apiKey: String,
    model: String,
    readTimeoutMillis: Long = 60000,
    connectTimeoutMillis: Long = 30000,
) : Deepseek(apiKey, model, readTimeoutMillis, connectTimeoutMillis) {
    override fun getEndpoint(): String {
        return "https://api.moonshot.ai/v1/chat/completions"
    }
}
