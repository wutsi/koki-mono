package com.wutsi.koki.portal.payment.model

import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class PaymentMethodCashModel(
    val id: String = "",
    val collectedBy: UserModel? = null,
    val collectedAt: Date? = null,
    val collectedAtText: String? = null,
)
