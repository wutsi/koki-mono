package com.wutsi.koki.tax.dto

data class SearchTaxProductResponse(
    val taxProducts: List<TaxProduct> = emptyList()
)
