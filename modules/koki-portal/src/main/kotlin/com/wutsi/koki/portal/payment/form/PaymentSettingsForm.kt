package com.wutsi.koki.portal.payment.form

import com.wutsi.koki.payment.dto.PaymentGateway

data class PaymentSettingsForm(
    val cash: Boolean = false,
    val check: Boolean = false,
    val interac: Boolean = false,
    val creditCard: Boolean = false,
    val creditCardGateway: PaymentGateway? = null,
    val paypal: Boolean = false,
    val mobile: Boolean = false,
    val mobileGateway: PaymentGateway? = null,
)
