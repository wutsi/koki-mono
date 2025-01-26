package com.wutsi.koki.portal.contact.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsListContactTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/contacts/types")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_LIST)
    }

    @Test
    fun import() {
        navigateTo("/settings/contacts/types")
        click(".btn-import")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/contacts/types")
        click(".btn-back")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS)
    }

    @Test
    fun `list - without permission contact-admin`() {
        setUpUserWithoutPermissions(listOf("contact:admin"))

        navigateTo("/settings/contacts/types")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
