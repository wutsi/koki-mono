package com.wutsi.koki.portal.refdata.model

import com.wutsi.koki.refdata.dto.CategoryType

data class CategoryModel(
    val id: Long = 0,
    val type: CategoryType = CategoryType.UNKNOWN,
    val parentId: Long? = null,
    val name: String = "",
    val longName: String = "",
    val level: Int = 0,
    val active: Boolean = true,
)
