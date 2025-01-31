package com.wutsi.koki.tax.dto

data class SearchTaxKpiResponse(
    val kpis: List<TaxKpiSummary> = emptyList()
)
