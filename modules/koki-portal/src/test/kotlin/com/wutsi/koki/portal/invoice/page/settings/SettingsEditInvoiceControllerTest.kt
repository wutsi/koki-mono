package com.wutsi.koki.portal.invoice.page.settings

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

class SettingsEditInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/invoices/edit")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_EDIT)

        input("#startNumber", "5555")
        select("#dueDays", 2)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("5555", request.firstValue.values[ConfigurationName.INVOICE_START_NUMBER])
        assertEquals("10", request.firstValue.values[ConfigurationName.INVOICE_DUE_DAYS])

        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
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

        navigateTo("/settings/invoices/edit")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS_EDIT)

        input("#startNumber", "5555")
        select("#dueDays", 2)
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.INVOICE_SETTINGS_EDIT)
        assertElementPresent("#alert-error")
    }

    @Test
    fun back() {
        navigateTo("/settings/invoices/edit")
        click(".btn-back")
        assertCurrentPageIs(PageName.INVOICE_SETTINGS)
    }

    @Test
    fun `edit - without permission invoice-admin`() {
        setUpUserWithoutPermissions(listOf("invoice:admin"))
        navigateTo("/settings/invoices/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/invoices/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
