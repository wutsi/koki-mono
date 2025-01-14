package com.wutsi.koki.email.dto

data class Email(
    val id: String = "",
    val senderId: Long = -1,
    val recipient: Recipient = Recipient(),
    val subject: String = "",
    val body: String = "",
    val attachments: List<Attachment> = emptyList(),
)
