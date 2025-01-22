package com.wutsi.koki.portal.email.model

import com.wutsi.koki.common.dto.ObjectType

data class EmailForm(
    val recipientType: ObjectType? = null,
    val accountId: Long? = null,
    val contactId: Long? = null,
    val subject: String = "",
    val body: String = "",
    val ownerType: ObjectType? = null,
    val ownerId: Long? = null,
    val attachmentFileId: List<Long> = emptyList()
)
