package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contactTypes
import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/contacts/create")

        assertCurrentPageIs(PageName.CONTACT_CREATE)

        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        select("#gender", 2)
        scrollToBottom()
        input("#phone", "+5147580000")
        input("#mobile", "+5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click("button[type=submit]")

        val request = argumentCaptor<CreateContactRequest>()
        verify(kokiContacts).create(request.capture())
        assertEquals(contactTypes[2].id, request.firstValue.contactTypeId)
        assertEquals("Yo", request.firstValue.firstName)
        assertEquals("Man", request.firstValue.lastName)
        assertEquals("Ms.", request.firstValue.salutations)
        assertEquals(Gender.FEMALE, request.firstValue.gender)
        assertEquals("+5147580000", request.firstValue.phone)
        assertEquals("+5147580011", request.firstValue.mobile)
        assertEquals("yo@gmail.com", request.firstValue.email)
        assertEquals("XX", request.firstValue.profession)
        assertEquals("EG", request.firstValue.employer)

        assertCurrentPageIs(PageName.CONTACT_SAVED)
        click(".btn-ok")
        assertCurrentPageIs(PageName.CONTACT_LIST)
    }

    @Test
    fun `create new`() {
        navigateTo("/contacts/create")

        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        select("#gender", 2)
        scrollToBottom()
        input("#phone", "+5147580000")
        input("#mobile", "+5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.CONTACT_SAVED)
        click(".btn-create")
        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun cancel() {
        navigateTo("/contacts/create")

        assertCurrentPageIs(PageName.CONTACT_CREATE)

        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        select("#gender", 2)
        scrollToBottom()
        input("#phone", "+5147580000")
        input("#mobile", "+5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.CONTACT_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiContacts).create(any())

        navigateTo("/contacts/create")

        assertCurrentPageIs(PageName.CONTACT_CREATE)

        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        select("#gender", 2)
        scrollToBottom()
        input("#phone", "+5147580000")
        input("#mobile", "+5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.CONTACT_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/contacts/create")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
