package com.wutsi.koki.payment.dto

enum class PaymentMethodType(val online: Boolean) {
    UNKNOWN(false),
    CASH(false),
    CHECK(false),
    INTERAC(false),
    CREDIT_CARD(true),
    MOBILE(true),
    BANK(false),
    PAYPAL(true),
}
