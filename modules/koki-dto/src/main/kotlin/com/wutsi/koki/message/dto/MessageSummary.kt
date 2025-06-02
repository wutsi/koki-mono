package com.wutsi.koki.message.dto

import com.wutsi.koki.common.dto.ObjectReference
import java.util.Date

data class MessageSummary(
    val id: Long = -1,
    val senderAccountId: Long? = null,
    val senderName: String = "",
    val senderEmail: String = "",
    val senderPhone: String? = null,
    val status: MessageStatus = MessageStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val owner: ObjectReference? = null,
    val body: String = "",
)
