package com.wutsi.koki.tax.dto

data class TaxKpiSummary(
    val userId: Long? = null,
    val taxId: Long? = null,
    val totalDuration: Double = 0.0,
)
