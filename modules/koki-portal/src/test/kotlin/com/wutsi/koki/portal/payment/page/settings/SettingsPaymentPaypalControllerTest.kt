package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
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
}
