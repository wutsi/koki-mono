package com.wutsi.koki.payment.dto

enum class PaymentGateway(val paymentMethodType: PaymentMethodType) {
    UNKNOWN(PaymentMethodType.UNKNOWN),
    STRIPE(PaymentMethodType.CREDIT_CARD),
    PAYPAL((PaymentMethodType.PAYPAL)),
    FLUTTERWAVE(PaymentMethodType.MOBILE),
}
