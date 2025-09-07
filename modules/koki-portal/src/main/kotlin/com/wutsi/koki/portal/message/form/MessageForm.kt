package com.wutsi.koki.portal.message.form

import com.wutsi.koki.common.dto.ObjectType

data class MessageForm(
    val conversationId: String? = null,
    val body: String? = null,

    @Deprecated("") val fromUserId: Long = -1,
    @Deprecated("") val toUserId: Long = -1,
    @Deprecated("") val ownerId: Long = -1,
    @Deprecated("") val ownerType: ObjectType = ObjectType.UNKNOWN,
)
