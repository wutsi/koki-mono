package com.wutsi.koki.email.dto

data class EmailSummary(
    val senderId: Long = -1,
    val recipient: Recipient = Recipient(),
    val subject: String = "",
)
