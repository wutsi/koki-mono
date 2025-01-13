package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.ObjectName
import kotlin.test.Test

class TaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/taxes/${tax.id}")
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiTaxes).delete(tax.id)
        assertCurrentPageIs(PageName.TAX_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.TAX_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiTaxes, never()).delete(any())
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiTaxes).delete(any())

        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.TAX)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun files() {
        navigateTo("/taxes/${tax.id}?tab=files")

        Thread.sleep(1000)
        verify(kokiFiles).files(
            anyOrNull(), // ids
            anyOrNull(), // workflpw-instance-id
            anyOrNull(), // form-id
            eq(tax.id), // owner-id
            eq(ObjectName.TAX), // owner-type
            eq(20), // limit
            eq(0), // offset
        )
        assertElementCount(".widget-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/taxes/${tax.id}?tab=notes")

        Thread.sleep(1000)
        verify(kokiNotes).notes(
            anyOrNull(), // ids
            eq(tax.id), // owner-id
            eq(ObjectName.TAX), // owner-type
            eq(20), // limit
            eq(0), // offset
        )
        assertElementCount(".widget-notes tr.note", NoteFixtures.notes.size)
    }

    @Test
    fun status() {
        navigateTo("/taxes/${tax.id}")

        click(".btn-status")
        assertCurrentPageIs(PageName.TAX_STATUS)
    }
}
