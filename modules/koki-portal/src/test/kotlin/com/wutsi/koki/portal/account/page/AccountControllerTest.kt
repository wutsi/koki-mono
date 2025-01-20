package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.ContactFixtures
import com.wutsi.koki.EmailFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class AccountControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/accounts/${account.id}")

        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/accounts/${account.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/accounts/${account.id}")
        assertCurrentPageIs(PageName.ACCOUNT_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.ACCOUNT_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/accounts/${account.id}")
        click(".btn-edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)
    }

    @Test
    fun contacts() {
        navigateTo("/accounts/${account.id}?tab=contact")

        Thread.sleep(1000)
        assertElementCount(".tab-contacts tr.contact", ContactFixtures.contacts.size)

        click(".btn-add-contact")
        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun files() {
        navigateTo("/accounts/${account.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files .file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/accounts/${account.id}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun taxes() {
        navigateTo("/accounts/${account.id}?tab=tax")

        Thread.sleep(1000)
        assertElementCount(".tab-taxes .tax", TaxFixtures.taxes.size)
    }

    @Test
    fun emails() {
        navigateTo("/accounts/${account.id}?tab=email")

        Thread.sleep(1000)
        assertElementCount(".tab-emails .email", EmailFixtures.emails.size)
    }
}
