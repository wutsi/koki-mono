package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.TaxFixtures.taxTypes
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class EditTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val request = argumentCaptor<UpdateTaxRequest>()
        verify(kokiTaxes).update(eq(tax.id), request.capture())
        val tax = request.firstValue
        assertEquals(LocalDate.now().year - 2, tax.fiscalYear)
        assertEquals(taxTypes[2].id, tax.taxTypeId)
//        assertEquals("2020-12-11", fmt.format(tax.startAt))
//        assertEquals("2020-12-21", fmt.format(tax.dueAt))
        assertEquals("This is a nice description", tax.description)

        assertCurrentPageIs(PageName.TAX_SAVED)
        click(".btn-ok")
        assertCurrentPageIs(PageName.TAX_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.TAX_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiTaxes).update(any(), any())

        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.TAX_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
