package com.wutsi.koki.refdata.dto

data class Amenity(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val nameFr: String? = null,
    val icon: String? = null,
    val active: Boolean = true,
)
