package com.wutsi.koki.platform.ai

interface ChatBot {
    fun complete(text: String): CompletionResponse
}
