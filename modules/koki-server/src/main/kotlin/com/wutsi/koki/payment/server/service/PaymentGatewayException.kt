package com.wutsi.koki.payment.server.service

class PaymentGatewayException(
    val errorCode: String, val supplierErrorCode: String?, message: String?, cause: Throwable? = null
) : Exception(message, cause)
