package com.wutsi.koki.portal.invoice.form

data class InvoiceNotificationSettingsForm(
    val enabled: Boolean = false,
    val subject: String? = null,
    val body: String? = null,
)
