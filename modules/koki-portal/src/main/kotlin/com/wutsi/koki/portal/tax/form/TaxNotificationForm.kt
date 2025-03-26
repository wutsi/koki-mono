package com.wutsi.koki.portal.tax.form

data class TaxNotificationForm(
    val enabled: Boolean = false,
    val type: TaxNotificationType? = null,
    val subject: String? = null,
    val body: String? = null,
)
