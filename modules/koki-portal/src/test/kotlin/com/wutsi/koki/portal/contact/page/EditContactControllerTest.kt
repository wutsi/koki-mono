package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.contact.dto.UpdateContactRequest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class EditContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/contacts/${contact.id}/edit")

        assertCurrentPageIs(PageName.CONTACT_EDIT)

        select2("#accountId", accounts[1].name)
        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        input("#phone", "5147580000")
        scrollToBottom()
        input("#mobile", "5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        select2("#language", "French")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateContactRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/contacts/${contact.id}"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals(accounts[1].id, request.firstValue.accountId)
        assertEquals(110L, request.firstValue.contactTypeId)
        assertEquals("Yo", request.firstValue.firstName)
        assertEquals("Man", request.firstValue.lastName)
        assertEquals("Ms.", request.firstValue.salutations)
        assertEquals("fr", request.firstValue.language)
        assertEquals("+15147580000", request.firstValue.phone)
        assertEquals("+15147580011", request.firstValue.mobile)
        assertEquals("yo@gmail.com", request.firstValue.email)
        assertEquals("XX", request.firstValue.profession)
        assertEquals("EG", request.firstValue.employer)

        assertCurrentPageIs(PageName.CONTACT)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `edit - full_access`() {
        setupUserWithFullAccessPermissions("contact")

        navigateTo("/contacts/${contact.id}/edit")
        assertCurrentPageIs(PageName.CONTACT_EDIT)

        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        input("#phone", "5147580000")
        scrollToBottom()
        input("#mobile", "5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        select2("#language", "French")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateContactRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/contacts/${contact.id}"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals(contact.accountId, request.firstValue.accountId)
        assertEquals(110L, request.firstValue.contactTypeId)
        assertEquals("Yo", request.firstValue.firstName)
        assertEquals("Man", request.firstValue.lastName)
        assertEquals("Ms.", request.firstValue.salutations)
        assertEquals("fr", request.firstValue.language)
        assertEquals("+15147580000", request.firstValue.phone)
        assertEquals("+15147580011", request.firstValue.mobile)
        assertEquals("yo@gmail.com", request.firstValue.email)
        assertEquals("XX", request.firstValue.profession)
        assertEquals("EG", request.firstValue.employer)

        assertCurrentPageIs(PageName.CONTACT)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/contacts/${contact.id}/edit")

        assertCurrentPageIs(PageName.CONTACT_EDIT)

        select2("#accountId", accounts[1].name)
        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        scrollToBottom()
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.CONTACT_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<UpdateContactRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/contacts/${contact.id}/edit")

        assertCurrentPageIs(PageName.CONTACT_EDIT)

        select2("#accountId", accounts[1].name)
        select("#contactTypeId", 3)
        input("#firstName", "Yo")
        input("#lastName", "Man")
        select("#salutation", 2)
        scrollToBottom()
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "yo@gmail.com")
        input("#profession", "XX")
        input("#employer", "EG")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.CONTACT_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/contacts/${contact.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `edit - without permission contact-manage`() {
        setupUserWithoutPermissions(listOf("contact:manage"))

        navigateTo("/contacts/${contact.id}/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
