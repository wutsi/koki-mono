package com.wutsi.koki.portal.invoice.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsInvoiceNotificationControllerTest : AbstractPageControllerTest() {
    @Test
    fun opened() {
        navigateTo("/settings/invoices/notifications/opened")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_NOTIFICATION)

        input("#subject", "This is the subject {{invoiceNumber}}")
        inputCodeMiror("<p>Hello</p>")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "This is the subject {{invoiceNumber}}",
            request.firstValue.values[ConfigurationName.INVOICE_EMAIL_SUBJECT]
        )
        assertEquals("<p>Hello</p>", request.firstValue.values[ConfigurationName.INVOICE_EMAIL_BODY])

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun paid() {
        navigateTo("/settings/invoices/notifications/paid")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_NOTIFICATION)

        input("#subject", "This is the subject {{invoiceNumber}}")
        inputCodeMiror("<p>Hello</p>")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "This is the subject {{invoiceNumber}}",
            request.firstValue.values[ConfigurationName.INVOICE_EMAIL_RECEIPT_SUBJECT]
        )
        assertEquals("<p>Hello</p>", request.firstValue.values[ConfigurationName.INVOICE_EMAIL_RECEIPT_BODY])

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
        assertElementVisible("#koki-toast")
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

        navigateTo("/settings/invoices/notifications/opened")
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.INVOICE_SETTINGS_NOTIFICATION)
        assertElementPresent("#alert-error")
    }

    @Test
    fun back() {
        navigateTo("/settings/invoices/notifications/opened")
        click(".btn-back")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
    }

    @Test
    fun `edit - without permission invoice-admin`() {
        setUpUserWithoutPermissions(listOf("invoice:admin"))
        navigateTo("/settings/invoices/notifications/paid")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/invoices/notifications/paid")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
