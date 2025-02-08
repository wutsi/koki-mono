package com.wutsi.koki.product.dto

data class SearchProductResponse(
    val products: List<ProductSummary> = emptyList()
)
