package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.EmailFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
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

        verify(rest).delete("$sdkBaseUrl/v1/taxes/${tax.id}")
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

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.TAX)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun files() {
        navigateTo("/taxes/${tax.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/taxes/${tax.id}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun emails() {
        navigateTo("/taxes/${tax.id}?tab=email")

        Thread.sleep(1000)
        assertElementCount(".tab-emails .email", EmailFixtures.emails.size)
    }

    @Test
    fun status() {
        navigateTo("/taxes/${tax.id}")

        click(".btn-status")
        assertCurrentPageIs(PageName.TAX_STATUS)
    }
}
