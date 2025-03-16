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

class SettingsPaymentCashControllerTest : AbstractPageControllerTest() {
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

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CASH)
        input("#instructions", "Instructions for payments")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED])
        assertEquals(
            "Instructions for payments",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS],
        )

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `cash - configure`() {
        navigateTo("/settings/payments")
        click(".btn-cash-configure", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_CASH)
        input("#instructions", "")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED])
        assertEquals("", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }
}
