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
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.FileFixtures
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

        verify(kokiContacts).contacts(
            anyOrNull(), // keywords
            anyOrNull(), // ids
            anyOrNull(), // contact-type-ids
            eq(listOf(account.id)), // account-ids
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

        verify(kokiFiles).files(
            anyOrNull(), // ids
            anyOrNull(), // workflpw-instance-id
            anyOrNull(), // form-id
            eq(contact.id), // owner-id
            eq("ACCOUNT"), // owner-type
            eq(20), // limit
            eq(0), // offset
        )
        assertElementCount(".widget-files tr.file", FileFixtures.files.size)
    }
}
