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

        verify(rest).delete("$sdkBaseUrl/v1/contacts/${contact.id}")
        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/contacts/${contact.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/contacts/${contact.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.CONTACT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun files() {
        navigateTo("/contacts/${contact.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/contacts/${contact.id}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun `show - without permission contact`() {
        setUpUserWithoutPermissions(listOf("contact"))

        navigateTo("/contacts/${contact.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `delete - without permission contact-delete`() {
        setUpUserWithoutPermissions(listOf("contact:delete"))

        navigateTo("/contacts/${contact.id}/delete")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
