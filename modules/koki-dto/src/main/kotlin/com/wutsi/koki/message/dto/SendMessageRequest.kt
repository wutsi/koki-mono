package com.wutsi.koki.message.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SendMessageRequest(
    @get:NotEmpty @get:Size(max = 100) val senderName: String = "",
    @get:NotEmpty @get:Size(max = 255) val senderEmail: String = "",
    @get:Size(max = 30) val senderPhone: String? = null,
    @get:Size(max = 2) val country: String? = null,
    @get:Size(max = 2) val language: String? = null,
    @get:NotEmpty val body: String = "",
    val owner: ObjectReference? = null
)
