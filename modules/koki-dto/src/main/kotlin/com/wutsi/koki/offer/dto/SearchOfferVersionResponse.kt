package com.wutsi.koki.offer.dto

data class SearchOfferVersionResponse(
    val offerVersions: List<OfferVersionSummary> = emptyList()
)
