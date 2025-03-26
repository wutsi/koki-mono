package com.wutsi.koki.portal.invoice.form

data class InvoiceNotificationForm(
    val enabled: Boolean = false,
    val subject: String? = null,
    val body: String? = null,
)
