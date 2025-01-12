package com.wutsi.koki.tax.dto

data class SearchTaxTypeResponse(
    val taxTypes: List<TaxTypeSummary> = emptyList()
)
