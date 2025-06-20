package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPaymentNotificationControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/payments/notifications")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_NOTIFICATION)

        input("#subject", "This is the subject {{paymentNumber}}")
        inputCodeMirror("<p>Hello</p>")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_EMAIL_ENABLED])
        assertEquals(
            "This is the subject {{paymentNumber}}",
            request.firstValue.values[ConfigurationName.PAYMENT_EMAIL_SUBJECT]
        )
        assertEquals("<p>Hello</p>", request.firstValue.values[ConfigurationName.PAYMENT_EMAIL_BODY])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 404, errorCode = ErrorCode.BUSINESS_NOT_FOUND)
        doThrow(ex).whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/configurations"),
                any<SaveConfigurationRequest>(),
                eq(Any::class.java)
            )

        navigateTo("/settings/payments/notifications")
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_NOTIFICATION)
        assertElementPresent("#alert-error")
    }

    @Test
    fun back() {
        navigateTo("/settings/payments/notifications")
        click(".btn-back")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `edit - without permission payment-admin`() {
        setupUserWithoutPermissions(listOf("payment:admin"))
        navigateTo("/settings/payments/notifications")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/payments/notifications")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
