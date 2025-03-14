package com.wutsi.koki.payment.server.service

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.server.service.gateway.StripeGatewayService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PaymentGatewayServiceProviderTest {
    val stripeGateway = mock<StripeGatewayService>()
    val provider = PaymentGatewayServiceProvider(
        stripe = stripeGateway
    )

    @Test
    fun stripe() {
        assertEquals(stripeGateway, provider.get(PaymentGateway.STRIPE))
    }

    @Test
    fun paypal() {
        assertThrows<PaymentGatewayException> { provider.get(PaymentGateway.PAYPAL) }
    }

    @Test
    fun flutterwave() {
        assertThrows<PaymentGatewayException> { provider.get(PaymentGateway.FLUTTERWAVE) }
    }

    @Test
    fun unknown() {
        assertThrows<PaymentGatewayException> { provider.get(PaymentGateway.UNKNOWN) }
    }
}
