package com.wutsi.koki.email.dto

import java.util.Date

data class EmailSummary(
    val id: String = "",
    val senderId: Long = -1,
    val recipient: Recipient = Recipient(),
    val subject: String = "",
    val summary: String = "",
    val createdAt: Date = Date(),
)
