package com.wutsi.koki.listing.dto

data class GetListingCqsResponse(
    val listingId: Long,
    val overallCqs: Int,
    val cqsBreakdown: ContentQualityScoreBreakdown,
)
