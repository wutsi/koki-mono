package com.wutsi.koki.chatbot.messenger.model

data class Messaging(
    val timestamp: Long = -1,
    val sender: Party = Party(),
    val recipient: Party = Party(),
    val message: Message? = null,
)
