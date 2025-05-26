package com.wutsi.koki.message.dto

import com.wutsi.koki.common.dto.ObjectReference
import java.util.Date

data class Message(
    val id: Long = -1,
    val senderName: String = "",
    val senderEmail: String = "",
    val senderPhone: String? = null,
    val status: MessageStatus = MessageStatus.UNKNOWN,
    val body: String = "",
    val createdAt: Date = Date(),
    val owner: ObjectReference? = null,
)
