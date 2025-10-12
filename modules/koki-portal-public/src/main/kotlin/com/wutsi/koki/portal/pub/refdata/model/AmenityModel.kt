package com.wutsi.koki.portal.pub.refdata.model

data class AmenityModel(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val icon: String? = null,
    val active: Boolean = true,
    val top: Boolean = false,
)
