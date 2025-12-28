package com.wutsi.koki.place.dto

data class PlaceSummary(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val neighbourhoodId: Long = -1,
    val cityId: Long = -1,
    val type: PlaceType = PlaceType.UNKNOWN,
    val status: PlaceStatus = PlaceStatus.UNKNOWN,
    val name: String = "",
    val summary: String? = null,
    val summaryFr: String? = null,
    val introduction: String? = null,
    val introductionFr: String? = null,
    val rating: Double? = null,
)
