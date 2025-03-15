package com.wutsi.koki.payment.server.service.gateway

import com.stripe.StripeClient
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service

@Service
class StripeClientBuilder(
    private val configurationService: ConfigurationService,
) {
    fun build(tenantId: Long): StripeClient {
        val apiKey = getApiKey(tenantId)
        return StripeClient(apiKey)
    }

    private fun getApiKey(tenantId: Long): String {
        val configs = configurationService.search(
            names = listOf(ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY),
            tenantId = tenantId,
        )
        return configs.firstOrNull()?.value
            ?: throw PaymentGatewayException(
                errorCode = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED,
                supplierErrorCode = null,
                message = "No API Kpi",
            )
    }
}
