package com.wutsi.koki.tax.dto

data class UpdateTaxStatusRequest(
    val status: TaxStatus = TaxStatus.NEW,
    val formId: Long? = null,
)
