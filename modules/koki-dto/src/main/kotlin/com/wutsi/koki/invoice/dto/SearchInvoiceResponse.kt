package com.wutsi.koki.invoice.dto

data class SearchInvoiceResponse(
    val invoices: List<InvoiceSummary> = emptyList()
)
