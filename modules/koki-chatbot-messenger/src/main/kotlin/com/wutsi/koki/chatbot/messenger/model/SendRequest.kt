package com.wutsi.koki.chatbot.messenger.model

data class SendRequest(
    val messaging_type: String = "RESPONSE",
    val recipient: Party = Party(),
    val message: Message? = null,
)
