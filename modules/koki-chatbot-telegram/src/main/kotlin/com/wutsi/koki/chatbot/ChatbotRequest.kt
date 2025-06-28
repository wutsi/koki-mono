package com.wutsi.koki.chatbot

data class ChatbotRequest(
    val query: String = "",
    val language: String = "fr",
    val country: String = "CM"
)
