package com.wutsi.koki.invoice.server.command

data class SendInvoiceCommand(
    val invoiceId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
)
