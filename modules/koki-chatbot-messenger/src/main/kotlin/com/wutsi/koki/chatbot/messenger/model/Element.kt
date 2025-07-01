package com.wutsi.koki.chatbot.messenger.model

data class Element(
    val title: String? = null,
    val imageUrl: String? = null,
    val subtitle: String? = null,
    val default_action: Button? = null,
    val buttons: List<Button> = emptyList()
)
