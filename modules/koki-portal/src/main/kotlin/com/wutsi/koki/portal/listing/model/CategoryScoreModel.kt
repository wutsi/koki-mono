package com.wutsi.koki.portal.listing.model

data class CategoryScoreModel(
    val score: Int = 0,
    val max: Int = 0,
) {
    val percentage: Int = if (max > 0) {
        score * 100 / max
    } else {
        0
    }

    val rating: String
        get() = if (percentage < 40) {
            "poor"
        } else if (percentage < 75) {
            "medium"
        } else if (percentage < 90) {
            "good"
        } else {
            "excellent"
        }
}
