package com.wutsi.koki.portal.listing.model

data class ListingCqsModel(
    val listingId: Long,
    val overallCqs: Int,
    val cqsBreakdown: ContentQualityScoreBreakdownModel,
)
