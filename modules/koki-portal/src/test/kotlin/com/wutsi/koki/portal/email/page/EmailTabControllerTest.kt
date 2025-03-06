package com.wutsi.koki.portal.email.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.EmailFixtures.emails
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test
import kotlin.jvm.java
import kotlin.test.assertEquals

class EmailTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/emails/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementCount(".tab-emails .email", emails.size)
        assertElementAttribute("#email-list", "data-owner-id", "111")
        assertElementAttribute("#email-list", "data-owner-type", "TAX")
    }

    @Test
    fun open() {
        navigateTo("/emails/tab?test-mode=true&owner-id=${account.id}&owner-type=ACCOUNT")

        click(".email a", 1000)
        assertElementVisible("#koki-modal")

        click("#btn-email-cancel")
        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `send to account`() {
        navigateTo("/emails/tab?test-mode=true&owner-id=${account.id}&owner-type=ACCOUNT")

        click("#btn-email-compose", 1000)
        assertElementVisible("#koki-modal")

        assertElementVisible("#account-selector")
        assertElementNotVisible("#contact-selector")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-email-submit", 1000)

        val request = argumentCaptor<SendEmailRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/emails"),
            request.capture(),
            eq(SendEmailResponse::class.java),
        )

        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(tax.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.owner?.type)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(account.name, request.firstValue.recipient.displayName)
        assertEquals(account.email, request.firstValue.recipient.email)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `send to contact`() {
        navigateTo("/emails/tab?test-mode=true&owner-id=${contact.id}&owner-type=CONTACT")

        click("#btn-email-compose", 1000)
        assertElementVisible("#koki-modal")

        assertElementNotVisible("#account-selector")
        assertElementVisible("#contact-selector")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-email-submit", 1000)

        val request = argumentCaptor<SendEmailRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/emails"),
            request.capture(),
            eq(SendEmailResponse::class.java),
        )

        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(contact.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.CONTACT, request.firstValue.owner?.type)
        assertEquals(contact.id, request.firstValue.recipient.id)
        assertEquals("${contact.firstName} ${contact.lastName}", request.firstValue.recipient.displayName)
        assertEquals(contact.email, request.firstValue.recipient.email)
        assertEquals(ObjectType.CONTACT, request.firstValue.recipient.type)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `send to tax`() {
        navigateTo("/emails/tab?test-mode=true&owner-id=${tax.id}&owner-type=TAX")

        click("#btn-email-compose", 1000)
        assertElementVisible("#koki-modal")

        assertElementVisible("#account-selector")
        assertElementNotVisible("#contact-selector")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-email-submit", 1000)

        val request = argumentCaptor<SendEmailRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/emails"),
            request.capture(),
            eq(SendEmailResponse::class.java),
        )

        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(tax.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.TAX, request.firstValue.owner?.type)
        assertEquals(tax.accountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `list - without permission email`() {
        setUpUserWithoutPermissions(listOf("email"))

        navigateTo("/emails/tab?test-mode=true&owner-id=${tax.id}&owner-type=TAX")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission email-send`() {
        setUpUserWithoutPermissions(listOf("email:send"))

        navigateTo("/emails/tab?test-mode=true&owner-id=${tax.id}&owner-type=TAX")
        assertElementNotPresent(".btn-email-compose")
    }
}
