package com.wutsi.koki.listing.dto

data class ContentQualityScoreBreakdown(
    val general: Int = 0,
    val legal: Int = 0,
    val amenities: Int = 0,
    val address: Int = 0,
    val geo: Int = 0,
    val rental: Int = 0,
    val images: Int = 0,
    val total: Int = 0,
)
