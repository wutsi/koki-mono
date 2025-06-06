package com.wutsi.koki.portal.refdata.model

data class AmenityModel(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val icon: String? = null,
    val active: Boolean = true,
)
