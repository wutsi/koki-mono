package com.wutsi.koki.portal.pub.place.model

import com.wutsi.koki.place.dto.RatingCriteria

data class PlaceRatingModel(
    val criteria: RatingCriteria = RatingCriteria.UNKNOWN,
    val value: Int = 0,
    val reason: String? = null,
) {

    val ratingPercentage: Int
        get() = (100 * value) / 5

    val ratingCode: String?
        get() = if (value < 2) {
            "danger"
        } else if (value < 3) {
            "warning"
        } else if (value < 4) {
            "info"
        } else {
            "success"
        }
}
