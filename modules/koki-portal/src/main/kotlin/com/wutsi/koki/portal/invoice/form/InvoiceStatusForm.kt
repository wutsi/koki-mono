package com.wutsi.koki.portal.invoice.form

import com.wutsi.koki.invoice.dto.InvoiceStatus

data class InvoiceStatusForm(
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val comment: String? = null,
)
