package com.wutsi.koki.refdata.dto

data class Category(
    val id: Long = 0,
    val type: CategoryType = CategoryType.UNKNOWN,
    val parentId: Long? = null,
    val name: String = "",
    val longName: String = "",
    val level: Int = 0,
    val active: Boolean = true,
)
