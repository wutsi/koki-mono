package com.wutsi.koki.portal.listing.model

data class ContentQualityScoreBreakdownModel(
    val general: CategoryScoreModel = CategoryScoreModel(),
    val legal: CategoryScoreModel = CategoryScoreModel(),
    val amenities: CategoryScoreModel = CategoryScoreModel(),
    val address: CategoryScoreModel = CategoryScoreModel(),
    val geo: CategoryScoreModel = CategoryScoreModel(),
    val rental: CategoryScoreModel = CategoryScoreModel(),
    val images: CategoryScoreModel = CategoryScoreModel(),
    val total: Int = 0,
)
