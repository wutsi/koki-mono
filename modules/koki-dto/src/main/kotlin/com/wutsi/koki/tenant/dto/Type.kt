package com.wutsi.koki.tenant.dto

import com.wutsi.koki.common.dto.ObjectType
import java.util.Date

data class Type(
    val id: Long = -1,
    val objectType: ObjectType = ObjectType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
