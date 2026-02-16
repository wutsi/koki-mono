package com.wutsi.koki.listing.dto

data class ContentQualityScoreBreakdown(
    val general: CategoryScore = CategoryScore(),
    val legal: CategoryScore = CategoryScore(),
    val amenities: CategoryScore = CategoryScore(),
    val address: CategoryScore = CategoryScore(),
    val geo: CategoryScore = CategoryScore(),
    val rental: CategoryScore = CategoryScore(),
    val images: CategoryScore = CategoryScore(),
    val total: Int = 0,
)
