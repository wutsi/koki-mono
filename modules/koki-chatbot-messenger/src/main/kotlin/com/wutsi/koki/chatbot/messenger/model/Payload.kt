package com.wutsi.koki.chatbot.messenger.model

data class Payload(
    val template_type: String? = null,
    val elements: List<Element>? = null,
    val buttons: List<Button>? = null,
)
