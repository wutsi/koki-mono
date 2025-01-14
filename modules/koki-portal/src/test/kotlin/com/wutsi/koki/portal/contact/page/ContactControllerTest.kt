package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/contacts/${contact.id}")
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/contacts/${contact.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/contacts/${contact.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiContacts).delete(contact.id)
        assertCurrentPageIs(PageName.CONTACT_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.CONTACT_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/contacts/${contact.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiContacts, never()).delete(any())
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiContacts).delete(any())

        navigateTo("/contacts/${contact.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.CONTACT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun files() {
        navigateTo("/contacts/${contact.id}?tab=files")

        Thread.sleep(1000)
        verify(kokiFiles).files(
            emptyList(), // ids
            emptyList(), // workflpw-instance-id
            emptyList(), // form-id
            contact.id, // owner-id
            ObjectType.ACCOUNT, // owner-type
            20, // limit
            0, // offset
        )
        assertElementCount(".widget-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/contacts/${contact.id}?tab=notes")

        Thread.sleep(1000)
        verify(kokiNotes).notes(
            emptyList(), // ids
            contact.id, // owner-id
            ObjectType.CONTACT, // owner-type
            20, // limit
            0, // offset
        )
        assertElementCount(".widget-notes tr.note", NoteFixtures.notes.size)
    }
}
