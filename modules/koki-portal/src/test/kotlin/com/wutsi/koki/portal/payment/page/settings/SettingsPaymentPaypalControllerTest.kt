package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPaymentPaypalControllerTest : AbstractPageControllerTest() {
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
}
