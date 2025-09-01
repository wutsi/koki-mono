package com.wutsi.koki.listing.dto

data class SearchListingResponse(
    val total: Long = -1L,
    val listings: List<ListingSummary> = emptyList()
)
