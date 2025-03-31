package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.stripe.exception.ApiException
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.service.StripeValidator
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPaymentCreditCardControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var validator: StripeValidator

    @Test
    fun `credit-card - disable`() {
        navigateTo("/settings/payments")
        click(".btn-credit-card-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `credit-card - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-credit-card-enable", 100)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CREDIT_CARD)
        select("#offline", 1)
        input("#offlinePhoneNumber", "5457580000")
        select("#gateway", 1)
        input("#stripeApiKey", "ST.123456780")
        click("button[type=submit]", 1000)

        verify(validator).validate("ST.123456780")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED])
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED])
        assertEquals(
            "5457580000",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER]
        )
        assertEquals("STRIPE", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY])
        assertEquals(
            "ST.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `credit-card - configure`() {
        navigateTo("/settings/payments")
        click(".btn-credit-card-configure", 100)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CREDIT_CARD)
        select("#offline", 0)
        select("#gateway", 1)
        input("#stripeApiKey", "ST.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED])
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED])
        assertEquals("STRIPE", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY])
        assertEquals(
            "ST.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `credit-card - Stripe validator error`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED))

        val ex = ApiException("", "", "", 409, IllegalStateException("failed"))
        doThrow(ex).whenever(validator).validate(any())

        navigateTo("/settings/payments")
        click(".btn-credit-card-enable")

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CREDIT_CARD)
        select("#offline", 1)
        input("#offlinePhoneNumber", "5457580000")
        select("#gateway", 1)
        input("#stripeApiKey", "ST.123456780")
        click("button[type=submit]", 1000)

        verify(rest, never()).postForEntity(
            any<String>(),
            any<SaveConfigurationRequest>(),
            eq(Any::class.java)
        )
        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CREDIT_CARD)
    }
}
