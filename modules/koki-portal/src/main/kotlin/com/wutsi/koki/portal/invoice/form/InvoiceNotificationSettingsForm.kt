package com.wutsi.koki.portal.invoice.form

data class InvoiceSettingsForm(
    val dueDays: Int = 0,
    val startNumber: Long = 0L,
)
