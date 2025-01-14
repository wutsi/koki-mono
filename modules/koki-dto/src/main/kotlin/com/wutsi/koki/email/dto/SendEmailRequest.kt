package com.wutsi.koki.email.dto

import com.wutsi.koki.common.dto.ObjectReference

data class SendEmailRequest(
    val to: Recipient = Recipient(),
    val subject: String = "",
    val body: String = "",
    val attachments: List<Attachment> = emptyList(),
    val owner: ObjectReference? = null,
)
