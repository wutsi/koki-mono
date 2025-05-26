package com.wutsi.koki.message.dto

data class UpdateMessageStatusRequest(
    val status: MessageStatus = MessageStatus.UNKNOWN,
)
