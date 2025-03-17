package com.wutsi.koki.payment.server.service.stripe

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class StripeClientBuilderTest {
    private val configurationService = mock<ConfigurationService>()
    private val builder = StripeClientBuilder(configurationService)

    @Test
    fun builder() {
        val config = ConfigurationEntity(
            name = ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY,
            value = "1234243"
        )
        doReturn(listOf(config)).whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        builder.build(111L)
    }

    @Test
    fun `on api key`() {
        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService)
            .search(anyOrNull(), anyOrNull(), anyOrNull())

        val result = assertThrows<PaymentGatewayException> { builder.build(111L) }

        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED, result.errorCode)
    }
}
