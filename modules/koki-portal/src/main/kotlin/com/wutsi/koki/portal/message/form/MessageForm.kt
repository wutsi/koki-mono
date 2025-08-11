package com.wutsi.koki.portal.message.form

import com.wutsi.koki.common.dto.ObjectType

data class MessageForm(
    val fromUserId: Long = -1,
    val toUserId: Long = -1,
    val ownerId: Long = -1,
    val ownerType: ObjectType = ObjectType.UNKNOWN,
    val body: String? = null,
)
