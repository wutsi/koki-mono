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

class SettingsPaymentMobileControllerTest : AbstractPageControllerTest() {
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
        select("#offline", 1)
        select("#offlineProvider", 3)
        input("#offlinePhoneNumber", "5457580000")
        input("#offlineAccountName", "Ray Sponsible")
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
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED])
        assertEquals(
            "MTN",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PROVIDER]
        )
        assertEquals(
            "Ray Sponsible",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ACCOUNT_NAME]
        )
        assertEquals(
            "5457580000",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER]
        )
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
        select("#offline", 0)
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
        assertEquals("", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED])
        assertEquals(
            "FL.123456780",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY]
        )
        assertEquals(
            "",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER]
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }
}
