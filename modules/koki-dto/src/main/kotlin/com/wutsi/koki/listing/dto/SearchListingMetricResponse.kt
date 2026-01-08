package com.wutsi.koki.listing.dto

data class SearchListingMetricResponse(
    val metrics: List<ListingMetricSummary> = emptyList(),
)
