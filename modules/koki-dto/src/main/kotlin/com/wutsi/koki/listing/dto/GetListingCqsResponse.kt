package com.wutsi.koki.listing.dto

data class GetListingCqsResponse(
    val listingId: Long = -1,
    val overallCqs: Int = -1,
    val cqsBreakdown: ContentQualityScoreBreakdown = ContentQualityScoreBreakdown(),
)
