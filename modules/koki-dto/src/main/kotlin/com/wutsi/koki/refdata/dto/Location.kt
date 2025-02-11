package com.wutsi.koki.refdata.dto

data class Location(
    val id: Long = -1,
    val parentId: Long? = null,
    val name: String = "",
    val type: LocationType = LocationType.UNKNOWN,
    val country: String,
    val population: Long = 0,
)
