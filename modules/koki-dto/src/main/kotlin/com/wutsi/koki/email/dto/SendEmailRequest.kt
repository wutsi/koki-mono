package com.wutsi.koki.email.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SendEmailRequest(
    @get:Valid val recipient: Recipient = Recipient(),
    @get:NotEmpty @get:Size(max = 255) val subject: String = "",
    val body: String = "",
    val attachmentFileIds: List<Long> = emptyList(),
    val owner: ObjectReference? = null,
    val data: Map<String, Any> = emptyMap()
)
