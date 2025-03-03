package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

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
}
