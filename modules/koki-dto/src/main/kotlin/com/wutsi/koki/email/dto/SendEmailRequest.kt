package com.wutsi.koki.email.dto

import com.wutsi.koki.common.dto.ObjectReference

data class SendEmailRequest(
    val recipient: Recipient = Recipient(),
    val subject: String = "",
    val body: String = "",
    val attachmentFileIds: List<Long> = emptyList(),
    val owner: ObjectReference? = null,
    val data: Map<String, Any> = emptyMap()
)
