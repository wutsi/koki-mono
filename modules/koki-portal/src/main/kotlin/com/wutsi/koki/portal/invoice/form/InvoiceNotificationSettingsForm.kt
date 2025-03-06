package com.wutsi.koki.portal.invoice.form

import com.wutsi.koki.portal.invoice.model.InvoiceNotificationType

data class InvoiceNotificationSettingsForm(
    val enabled: Boolean = false,
    val subject: String? = null,
    val body: String? = null,
    val type: InvoiceNotificationType? = null,
)
