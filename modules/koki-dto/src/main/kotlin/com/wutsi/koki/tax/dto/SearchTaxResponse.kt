package com.wutsi.koki.tax.dto

data class SearchTaxResponse(
    val taxes: List<TaxSummary> = emptyList()
)
