package com.wutsi.koki.portal.listing.model

data class CategoryScoreModel(
    val score: Int = 0,
    val max: Int = 0,
    val rating: String = ""
) {
    val percentage: Int = if (max > 0) {
        score * 100 / max
    } else {
        0
    }
}
