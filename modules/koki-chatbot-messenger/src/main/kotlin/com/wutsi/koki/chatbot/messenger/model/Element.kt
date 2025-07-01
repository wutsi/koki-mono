package com.wutsi.koki.chatbot.messenger.model

data class Element(
    val title: String? = null,
    val image_url: String? = null,
    val subtitle: String? = null,
    val default_action: Button? = null,
    val buttons: List<Button>? = null
)
