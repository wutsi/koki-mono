package com.wutsi.koki.chatbot.messenger.model

data class Attachment(
    val type: String = "",
    val payload: Payload = Payload(),
)
