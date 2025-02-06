package com.wutsi.koki.product.dto

data class SearchPriceResponse(
    val prices: List<PriceSummary> = emptyList()
)
