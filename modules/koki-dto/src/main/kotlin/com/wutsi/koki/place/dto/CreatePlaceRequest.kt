package com.wutsi.koki.place.dto

data class CreatePlaceRequest(
    val name: String = "",
    val type: PlaceType = PlaceType.UNKNOWN,
    val neighbourhoodId: Long = -1,
)
