package com.wutsi.koki.invoice.dto.event

import com.wutsi.koki.invoice.dto.InvoiceStatus

data class InvoiceStatusChangedEvent(
    val invoiceId: Long = -1,
    val tenantId: Long = -1,
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis()
)
