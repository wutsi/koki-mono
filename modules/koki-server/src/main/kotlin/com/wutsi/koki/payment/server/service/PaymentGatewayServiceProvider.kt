package com.wutsi.koki.payment.server.service

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.server.service.gateway.StripeGatewayService
import org.springframework.stereotype.Service

@Service
class PaymentGatewayServiceProvider(
    private val stripe: StripeGatewayService
) {
    @Throws(PaymentGatewayException::class)
    fun get(gateway: PaymentGateway): PaymentGatewayService {
        return getOrNull(gateway) ?: throw PaymentGatewayException(
            errorCode = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED,
            supplierErrorCode = null,
            message = "Gateway not supported: $gateway"
        )
    }

    fun getOrNull(gateway: PaymentGateway): PaymentGatewayService? {
        return when (gateway) {
            PaymentGateway.STRIPE -> stripe
            else -> null
        }
    }
}
