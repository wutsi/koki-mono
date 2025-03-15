package com.wutsi.koki.portal.payment.form

import com.wutsi.koki.payment.dto.PaymentGateway

data class PaymentSettingsCreditCardForm(
    val offline: Boolean = false,
    val phoneNumber: String? = null,
    val gateway: PaymentGateway? = null,
    val stripeApiKey: String? = null,
)
