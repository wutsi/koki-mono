package com.wutsi.koki.invoice.dto

data class UpdateInvoiceStatusRequest(
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val comment: String? = null,
)
