package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `show - without permission invoice-admin`() {
        setUpUserWithoutPermissions(listOf("payment:admin"))
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun back() {
        navigateTo("/settings/payments")
        click(".btn-back")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `cash - disable`() {
        navigateTo("/settings/payments")
        click(".btn-cash-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_CASH_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `cash - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_CASH_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-cash-enable", 100)

        assertConfig("1", ConfigurationName.PAYMENT_METHOD_CASH_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `check - disable`() {
        navigateTo("/settings/payments")
        click(".btn-check-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `check - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-check-enable", 100)

        assertConfig("1", ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `interac - disable`() {
        navigateTo("/settings/payments")
        click(".btn-interac-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `interac - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-interac-enable", 100)

        assertConfig("1", ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

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
        select("#gateway", 1)
        input("#stripeApiKey", "ST.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED])
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
        select("#gateway", 1)
        input("#stripeApiKey", "ST.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED])
        assertEquals("STRIPE", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY])
        assertEquals(
            "ST.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `paypal - disable`() {
        navigateTo("/settings/payments")
        click(".btn-paypal-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `paypal - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-paypal-enable", 100)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_PAYPAL)
        input("#clientId", "CL.123456780")
        input("#secretKey", "SK.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED])
        assertEquals("CL.123456780", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID])
        assertEquals("SK.123456780", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_SECRET_KEY])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `paypal - configure`() {
        navigateTo("/settings/payments")
        click(".btn-paypal-configure", 100)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_PAYPAL)
        input("#clientId", "CL.123456780")
        input("#secretKey", "SK.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED])
        assertEquals("CL.123456780", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID])
        assertEquals("SK.123456780", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_PAYPAL_SECRET_KEY])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `mobile - disable`() {
        navigateTo("/settings/payments")
        click(".btn-mobile-disable", 1000)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `mobile - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-mobile-enable", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_MOBILE)
        select("#gateway", 1)
        input("#flutterwaveSecretKey", "FL.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED])
        assertEquals(
            "FL.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `mobile - configure`() {
        navigateTo("/settings/payments")
        click(".btn-mobile-configure", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_MOBILE)
        select("#gateway", 1)
        input("#flutterwaveSecretKey", "FL.123456780")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED])
        assertEquals(
            "FL.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    private fun assertConfig(expectedValue: String, name: String) {
        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(expectedValue, request.firstValue.values[name])
    }

    private fun disableConfig(names: List<String>) {
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(
                    configurations = TenantFixtures.config
                        .filter { cfg -> !names.contains(cfg.key) }
                        .map { cfg ->
                            Configuration(
                                name = cfg.key,
                                value = cfg.value
                            )
                        }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }
}
