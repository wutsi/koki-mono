package com.wutsi.koki.portal.file.page.settings

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsFileControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/files")
        assertCurrentPageIs(PageName.FILE_SETTINGS)
    }

    @Test
    fun storage() {
        navigateTo("/settings/files")
        click(".btn-storage")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
    }

    @Test
    fun `show - without permission file-admin`() {
        setUpUserWithoutPermissions(listOf("file:admin"))
        navigateTo("/settings/files")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/files")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
