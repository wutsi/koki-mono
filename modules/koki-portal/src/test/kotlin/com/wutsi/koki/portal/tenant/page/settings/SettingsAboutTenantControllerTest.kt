package com.wutsi.koki.portal.tenant.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsAboutTenantControllerTest : AbstractPageControllerTest() {
    @Test
    fun about() {
        navigateTo("/settings/tenant/about")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_ABOUT)
    }

    @Test
    fun back() {
        navigateTo("/settings/tenant/about")
        click(".btn-back")
        assertCurrentPageIs(PageName.TENANT_SETTINGS)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/tenant/about")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission tenant-admin`() {
        navigateTo("/settings/tenant/about")

        navigateTo("/settings/tenant")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
