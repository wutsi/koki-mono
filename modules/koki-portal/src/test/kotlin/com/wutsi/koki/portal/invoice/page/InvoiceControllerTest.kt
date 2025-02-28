package com.wutsi.koki.portal.invoice.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.InvoiceFixtures.invoice
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ShowInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
    }

    @Test
    fun products() {
        navigateTo("/invoices/${invoice.id}?tab=invoice-product")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementCount("tr.invoice-item", invoice.items.size)
        assertElementCount("tr.invoice-tax", 2)
    }

    @Test
    fun file() {
        navigateTo("/invoices/${invoice.id}?tab=file")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementCount("tr.file", files.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/invoices/${invoice.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission invoice-manage`() {
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotPresent(".btn-approve")
        assertElementNotPresent(".btn-payment")
    }

    @Test
    fun `show - without permission invoice-void`() {
        setUpUserWithoutPermissions(listOf("invoice:void"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotPresent(".btn-void")
    }
}
