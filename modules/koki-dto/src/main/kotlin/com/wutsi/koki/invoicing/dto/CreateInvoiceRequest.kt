package com.wutsi.koki.invoicing.dto

import java.util.Date

data class CreateInvoiceRequest(
    val accountId: Long = -1,
    val invoicedAt: Date = Date(),
    val dueAt: Date? = null,
)
