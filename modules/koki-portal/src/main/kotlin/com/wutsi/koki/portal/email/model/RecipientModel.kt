package com.wutsi.koki.portal.email.model

import com.wutsi.koki.common.dto.ObjectType

data class RecipientModel(
    val id: Long? = null,
    val type: ObjectType = ObjectType.UNKNOWN,
    val name: String = "",
    val email: String? = null,
)
