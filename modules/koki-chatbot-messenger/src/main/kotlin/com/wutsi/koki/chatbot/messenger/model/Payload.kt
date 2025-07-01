package com.wutsi.koki.chatbot.messenger.model

data class Payload(
    val messaging_type: String = "RESPONSE",
    val recipient: Party = Party(),
    val message: Message? = null,
    val template_type: String? = null,
    val buttons: List<Button> = emptyList(),
    val elements: List<Element> = emptyList(),
)
