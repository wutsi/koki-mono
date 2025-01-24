package com.wutsi.koki.portal.contact.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/contacts/types/${contact.id}")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE)
    }

    @Test
    fun back() {
        navigateTo("/settings/contacts/types/${contact.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_LIST)
    }
}
