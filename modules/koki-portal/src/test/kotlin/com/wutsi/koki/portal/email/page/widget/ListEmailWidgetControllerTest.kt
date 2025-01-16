package com.wutsi.koki.portal.email.page.widget

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.EmailFixtures.emails
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListEmailWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/emails/widgets/list?test-mode=true")

        assertElementCount(".widget-emails .email", emails.size)
    }

    @Test
    fun open() {
        navigateTo("/emails/widgets/list?test-mode=true")

        click(".email a", 1000)
        assertElementVisible("#email-modal")

        click("#btn-email-cancel")
        assertElementNotVisible("#email-modal")
    }

    @Test
    fun `send to account`() {
        navigateTo("/emails/widgets/list?test-mode=true&owner-id=${tax.id}&owner-type=TAX&recipient-id=${account.id}&recipient-type=ACCOUNT")

        click("#btn-email-compose", 1000)
        assertElementVisible("#email-modal")

        assertElementVisible("#account-selector")
        assertElementNotVisible("#contact-selector")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-email-submit", 1000)

        val request = argumentCaptor<SendEmailRequest>()
        verify(kokiEmails).send(request.capture())
        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(tax.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.TAX, request.firstValue.owner?.type)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)

        assertElementNotVisible("#email-modal")
    }

    @Test
    fun `send to contact`() {
        navigateTo("/emails/widgets/list?test-mode=true&owner-id=${tax.id}&owner-type=TAX&recipient-id=${contact.id}&recipient-type=CONTACT")

        click("#btn-email-compose", 1000)
        assertElementVisible("#email-modal")

        assertElementNotVisible("#account-selector")
        assertElementVisible("#contact-selector")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-email-submit", 1000)

        val request = argumentCaptor<SendEmailRequest>()
        verify(kokiEmails).send(request.capture())
        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(tax.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.TAX, request.firstValue.owner?.type)
        assertEquals(contact.id, request.firstValue.recipient.id)
        assertEquals(ObjectType.CONTACT, request.firstValue.recipient.type)

        assertElementNotVisible("#email-modal")
    }
}
