package com.wutsi.koki.portal.common.model

import com.wutsi.koki.common.dto.ObjectType

data class ObjectReferenceModel(
    val id: Long = -1,
    val type: ObjectType = ObjectType.UNKNOWN,
    val title: String = "",
    val imageUrl: String? = null,
    val url: String = "",
)
