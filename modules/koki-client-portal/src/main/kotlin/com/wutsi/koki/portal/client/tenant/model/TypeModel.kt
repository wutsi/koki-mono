package com.wutsi.koki.portal.client.tenant.model

import com.wutsi.koki.common.dto.ObjectType

data class TypeModel(
    val id: Long = -1,
    val name: String = "",
    val objectType: ObjectType = ObjectType.UNKNOWN,
    val title: String = "",
    val description: String? = null,
    val active: Boolean = false,
)
