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

class SettingsPaymentCheckControllerTest : AbstractPageControllerTest() {
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

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CHECK)
        input("#payee", "Ray Sponsible")
        input("#instructions", "Instructions for payments")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED])
        assertEquals("RAY SPONSIBLE", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE])
        assertEquals(
            "Instructions for payments",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS],
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `check - configure`() {
        navigateTo("/settings/payments")
        click(".btn-check-configure", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CHECK)
        input("#payee", "Ray Sponsible")
        input("#instructions", "")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED])
        assertEquals("RAY SPONSIBLE", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE])
        assertEquals("", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }
}
