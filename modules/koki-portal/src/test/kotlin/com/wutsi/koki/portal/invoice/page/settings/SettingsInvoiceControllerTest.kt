package com.wutsi.koki.portal.invoice.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/invoices")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
    }

    @Test
    fun edit() {
        navigateTo("/settings/invoices")
        click(".btn-edit")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_EDIT)
    }

    @Test
    fun `show - without permission invoice-admin`() {
        setUpUserWithoutPermissions(listOf("invoice:admin"))
        navigateTo("/settings/invoices")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/invoices")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun back() {
        navigateTo("/settings/invoices")
        click(".btn-back")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `notification opened`() {
        navigateTo("/settings/invoices")
        click(".btn-notification-opened")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_NOTIFICATION)
    }

    @Test
    fun `enable notification`() {
        navigateTo("/settings/invoices")
        click(".btn-notification-paid-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "1",
            request.firstValue.values[ConfigurationName.INVOICE_EMAIL_RECEIPT_ENABLED]
        )

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `disable notification`() {
        navigateTo("/settings/invoices")
        click(".btn-notification-opened-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "",
            request.firstValue.values[ConfigurationName.INVOICE_EMAIL_ENABLED]
        )

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
        assertElementVisible("#koki-toast")
    }
}
