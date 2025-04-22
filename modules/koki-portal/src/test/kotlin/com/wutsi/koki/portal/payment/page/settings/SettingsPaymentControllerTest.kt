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

class SettingsPaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `show - without permission payment-admin`() {
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
    fun `enable notification`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_EMAIL_ENABLED))

        navigateTo("/settings/payments")
        scrollToBottom()
        click(".btn-notification-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_EMAIL_ENABLED])
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `enable notification not configured`() {
        disableConfig(
            listOf(
                ConfigurationName.PAYMENT_EMAIL_ENABLED,
                ConfigurationName.PAYMENT_EMAIL_SUBJECT,
                ConfigurationName.PAYMENT_EMAIL_BODY,
            )
        )

        navigateTo("/settings/payments")
        scrollToBottom()
        click(".btn-notification-enable")

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_NOTIFICATION)
    }

    @Test
    fun `disable notification`() {
        navigateTo("/settings/payments")
        scrollToBottom()
        click(".btn-notification-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.PAYMENT_EMAIL_ENABLED])
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun notification() {
        navigateTo("/settings/payments")
        scrollToBottom()
        click(".btn-notification")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_NOTIFICATION)
    }
}
