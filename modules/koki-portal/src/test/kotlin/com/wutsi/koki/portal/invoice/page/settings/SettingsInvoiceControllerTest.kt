package com.wutsi.koki.portal.invoice.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.portal.AbstractPageControllerTest
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
        assertCurrentPageIs(PageName.ERROR_403)
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
    fun `notification configure`() {
        navigateTo("/settings/invoices")
        click(".btn-configure")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_NOTIFICATION)
    }

    @Test
    fun `notification enable`() {
        disableConfig(listOf(ConfigurationName.INVOICE_EMAIL_ENABLED))

        navigateTo("/settings/invoices")
        click(".btn-notification-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.INVOICE_EMAIL_ENABLED])

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
    }

    @Test
    fun `notification enable and configure`() {
        disableConfig(
            listOf(
                ConfigurationName.INVOICE_EMAIL_ENABLED,
                ConfigurationName.INVOICE_EMAIL_SUBJECT,
                ConfigurationName.INVOICE_EMAIL_BODY,
            )
        )

        navigateTo("/settings/invoices")
        click(".btn-notification-enable")

        disableConfig(listOf(PageName.INVOICE_SETTINGS_NOTIFICATION))
    }

    @Test
    fun `notification disable`() {
        navigateTo("/settings/invoices")
        click(".btn-notification-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.INVOICE_EMAIL_ENABLED])

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
    }
}
