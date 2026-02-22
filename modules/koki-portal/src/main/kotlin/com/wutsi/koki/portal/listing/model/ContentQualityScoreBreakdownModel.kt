package com.wutsi.koki.portal.listing.model

data class ContentQualityScoreBreakdownModel(
    val general: CategoryScoreModel = CategoryScoreModel(),
    val legal: CategoryScoreModel = CategoryScoreModel(),
    val amenities: CategoryScoreModel = CategoryScoreModel(),
    val address: CategoryScoreModel = CategoryScoreModel(),
    val geo: CategoryScoreModel = CategoryScoreModel(),
    val leasing: CategoryScoreModel = CategoryScoreModel(),
    val image: CategoryScoreModel = CategoryScoreModel(),
    val total: Int = 0,
    val rating: String = ""
)
