package com.wutsi.koki.place.dto

data class PlaceSummary(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val neighbourhoodId: Long? = null,
    val type: PlaceType = PlaceType.UNKNOWN,
    val name: String = "",
    val nameFr: String? = null,
    val summary: String? = null,
    val summaryFr: String? = null,
    val rating: Double? = null,
)
