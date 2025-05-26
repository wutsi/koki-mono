package com.wutsi.koki.message.dto.event

import com.wutsi.koki.common.dto.ObjectReference

data class MessageSentEvent(
    val messageId: Long = -1,
    val tenantId: Long = -1,
    val owner: ObjectReference? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
