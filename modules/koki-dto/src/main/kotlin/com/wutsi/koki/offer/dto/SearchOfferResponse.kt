package com.wutsi.koki.offer.dto

data class SearchOfferResponse(
    val offers: List<OfferSummary> = emptyList()
)
