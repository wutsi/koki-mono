package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.FileFixtures
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

        verify(kokiFiles).files(
            anyOrNull(), // ids
            anyOrNull(), // workflpw-instance-id
            anyOrNull(), // form-id
            eq(contact.id), // owner-id
            eq("CONTACT"), // owner-type
            eq(20), // limit
            eq(0), // offset
        )
        assertElementCount(".widget-files tr.file", FileFixtures.files.size)
    }
}
