package com.wutsi.koki.portal.contact.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/contacts")

        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementCount("tr.contact", contacts.size)
    }

    @Test
    fun `list - with full_access permission`() {
        setupUserWithFullAccessPermissions("contact")

        navigateTo("/contacts")
        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementPresent(".btn-create")
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<ContactSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(contacts[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchContactResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchContactResponse::class.java)
            )

        navigateTo("/contacts")

        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementCount("tr.contact", entries.size)

        scrollToBottom()
        click("#contact-load-more a")
        assertElementCount("tr.contact", 2 * entries.size)
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
        click("tr.contact a")

        val windowHandles = driver.getWindowHandles().toList()
        driver.switchTo().window(windowHandles[1]);
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun create() {
        navigateTo("/contacts")
        click(".btn-create")
        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun `list - without permission contact`() {
        setupUserWithoutPermissions(listOf("contact"))

        navigateTo("/contacts")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - without permission contact-manage`() {
        setupUserWithoutPermissions(listOf("contact:manage"))

        navigateTo("/contacts")

        assertCurrentPageIs(PageName.CONTACT_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}
