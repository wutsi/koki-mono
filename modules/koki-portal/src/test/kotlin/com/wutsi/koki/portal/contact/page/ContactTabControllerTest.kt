package com.wutsi.koki.portal.contact.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contacts
import kotlin.test.Test

class ContactTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/contacts/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount("tr.contact", contacts.size)
    }
}
