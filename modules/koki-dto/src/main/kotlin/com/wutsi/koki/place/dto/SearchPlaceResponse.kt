package com.wutsi.koki.place.dto

data class SearchPlaceResponse(
    val places: List<PlaceSummary> = emptyList(),
)
