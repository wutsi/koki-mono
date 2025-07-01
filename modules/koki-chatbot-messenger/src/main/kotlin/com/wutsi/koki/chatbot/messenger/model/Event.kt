package com.wutsi.koki.chatbot.messenger.model

data class Event(
    val `object`: String,
    val entry: List<Entry> = emptyList(),
)
