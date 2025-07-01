package com.wutsi.koki.chatbot.messenger.model

data class Entry(
    val id: String = "",
    val time: Long = -1,
    val messaging: List<Messaging> = emptyList(),
)
