package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ListContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/contacts")

        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementCount("tr.contact", contacts.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<ContactSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(contacts[0].copy(id = ++seed))
        }
        doReturn(SearchContactResponse(entries))
            .doReturn(SearchContactResponse(contacts))
            .whenever(kokiContacts)
            .contacts(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/contacts")

        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementCount("tr.contact", entries.size)

        scrollToBottom()
        click("#contact-load-more a", 1000)
        assertElementCount("tr.contact", entries.size + contacts.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/contacts")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun show() {
        navigateTo("/contacts")
        click(".btn-view")
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun edit() {
        navigateTo("/contacts")
        click(".btn-edit")
        assertCurrentPageIs(PageName.CONTACT_EDIT)
    }
}
