package com.wutsi.koki.portal.pub.place.model

import com.wutsi.koki.place.dto.RatingCriteria

data class PlaceRatingModel(
    val criteria: RatingCriteria = RatingCriteria.UNKNOWN,
    val value: Int = 0,
    val reason: String? = null,
)
