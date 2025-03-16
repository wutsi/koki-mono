package com.wutsi.koki.portal.payment.form

data class PaymentNotificationSettingsForm(
    val enabled: Boolean = false,
    val subject: String? = null,
    val body: String? = null,
)
