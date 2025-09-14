package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ContactTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount("tr.contact", contacts.size)
        assertElementPresent(".btn-add-contact")
    }

    @Test
    fun `list - full_access`() {
        setupUserWithFullAccessPermissions("contact")

        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount("tr.contact", contacts.size)
        assertElementPresent(".btn-add-contact")
    }

    @Test
    fun `AddContact button not displayed from Tax`() {
        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementNotPresent(".btn-add-contact")
    }

    @Test
    fun addNew() {
        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click(".btn-add-contact")

        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun view() {
        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click("tr.contact a")

        val windowHandles = driver.getWindowHandles().toList()
        driver.switchTo().window(windowHandles[1])
        assertCurrentPageIs(PageName.CONTACT)
    }

    @Test
    fun `list - without permission contact-manage`() {
        setupUserWithoutPermissions(listOf("contact:manage"))

        navigateTo("/contacts/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-add-contact")
        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun `list - without permission contact`() {
        setupUserWithoutPermissions(listOf("contact"))

        navigateTo("/contacts/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
