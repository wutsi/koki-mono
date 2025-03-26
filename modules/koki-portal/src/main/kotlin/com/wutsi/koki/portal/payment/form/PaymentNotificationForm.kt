package com.wutsi.koki.portal.payment.form

data class PaymentNotificationForm(
    val enabled: Boolean = false,
    val subject: String? = null,
    val body: String? = null,
)
