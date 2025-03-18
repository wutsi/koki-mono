package com.wutsi.koki.portal.payment.form

import com.wutsi.koki.payment.dto.PaymentGateway

data class PaymentSettingsMobileForm(
    val offline: Boolean = false,
    val offlinePhoneNumber: String? = null,
    val offlineAccountName: String? = null,
    val offlineProvider: String? = null,
    val gateway: PaymentGateway? = null,
    val flutterwaveSecretKey: String? = null
)
