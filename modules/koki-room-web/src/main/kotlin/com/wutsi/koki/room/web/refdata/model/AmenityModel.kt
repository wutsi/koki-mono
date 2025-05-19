package com.wutsi.koki.room.web.refdata.model

data class AmenityModel(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val icon: String? = null,
    val active: Boolean = true,
)
