package com.wutsi.koki.portal.payment.form

import com.wutsi.koki.payment.dto.PaymentGateway

data class PaymentSettingsMobileForm(
    val gateway: PaymentGateway? = null,
    val flutterwaveSecretKey: String? = null
)
