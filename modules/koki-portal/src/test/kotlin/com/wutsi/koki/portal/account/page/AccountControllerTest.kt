package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.ContactFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.ObjectName
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

        verify(kokiAccounts).delete(account.id)
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

        verify(kokiAccounts, never()).delete(any())
        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiAccounts).delete(any())

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
        navigateTo("/accounts/${account.id}?tab=contacts")

        Thread.sleep(1000)
        verify(kokiContacts).contacts(
            anyOrNull(), // keywords
            anyOrNull(), // ids
            anyOrNull(), // contact-type-ids
            eq(listOf(account.id)), // account-ids
            anyOrNull(), // created-by-id
            eq(20), // limit
            eq(0), // offset
        )
        assertElementCount(".widget-contacts tr.contact", ContactFixtures.contacts.size)

        click(".btn-add-contact")
        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun files() {
        navigateTo("/accounts/${account.id}?tab=files")

        Thread.sleep(1000)
        verify(kokiFiles).files(
            emptyList(), // ids
            emptyList(), // workflow-instance-id
            emptyList(), // form-id
            account.id, // owner-id
            ObjectName.ACCOUNT, // owner-type
            20, // limit
            0, // offset
        )
        assertElementCount(".widget-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/accounts/${account.id}?tab=notes")

        Thread.sleep(1000)
        verify(kokiNotes).notes(
            emptyList(), // ids
            account.id, // owner-id
            ObjectName.ACCOUNT, // owner-type
            20, // limit
            0, // offset
        )
        assertElementCount(".widget-notes tr.note", NoteFixtures.notes.size)
    }

    @Test
    fun taxes() {
        navigateTo("/accounts/${account.id}?tab=taxes")

        Thread.sleep(1000)
        verify(kokiTaxes).taxes(
            emptyList(), // ids
            emptyList(), // tsx-type-id
            listOf(account.id), // account-id
            emptyList(), // participant-id
            emptyList(), // assignee-id
            emptyList(), // created-by-id
            emptyList(), // statuses
            20, // limit
            0, // offset
        )
        assertElementCount(".widget-taxes tr.taxe", TaxFixtures.taxes.size)
    }
}
