package com.wutsi.koki.email.dto

import java.util.Date

data class Email(
    val id: String = "",
    val senderId: Long = -1,
    val recipient: Recipient = Recipient(),
    val subject: String = "",
    val body: String = "",
    val attachmentFileIds: List<Long> = emptyList(),
    val createdAt: Date = Date()
)
