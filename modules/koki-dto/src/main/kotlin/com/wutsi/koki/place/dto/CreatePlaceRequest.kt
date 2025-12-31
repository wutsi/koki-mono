package com.wutsi.koki.place.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreatePlaceRequest(
    @get:NotEmpty @get:Size(max = 100) val name: String = "",
    val type: PlaceType = PlaceType.UNKNOWN,
    val neighbourhoodId: Long = -1,
    val generateContent: Boolean = true,
)
