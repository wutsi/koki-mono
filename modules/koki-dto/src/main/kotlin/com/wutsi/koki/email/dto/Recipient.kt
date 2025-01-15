package com.wutsi.koki.email.dto

import com.wutsi.koki.common.dto.ObjectType

data class Recipient(
    val id: Long = -1,
    val type: ObjectType = ObjectType.UNKNOWN,
)
