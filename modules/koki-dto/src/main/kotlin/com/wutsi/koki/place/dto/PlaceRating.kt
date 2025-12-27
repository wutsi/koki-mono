package com.wutsi.koki.place.dto

data class PlaceRating(
    val criteria: RatingCriteria = RatingCriteria.UNKNOWN,
    val value: Int = 0,
    val reason: String? = null,
)
