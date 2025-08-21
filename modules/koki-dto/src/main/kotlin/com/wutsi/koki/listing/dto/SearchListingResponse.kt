package com.wutsi.koki.listing.dto

data class SearchListingResponse(
    val listings: List<ListingSummary> = emptyList()
)
