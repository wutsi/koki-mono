package com.wutsi.koki.tax.dto

data class UpdateTaxStatusRequest(
    val status: TaxStatus = TaxStatus.NEW,
    val assigneeId: Long? = null,
    val notes: String? = null,
)
