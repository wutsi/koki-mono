package com.wutsi.koki.chatbot.messenger.model

data class Element(
    val title: String = "",
    val imageUrl: String = "",
    val subtitle: String? = null,
    val default_action: Button? = null,
    val buttons: List<Button> = emptyList()
)
