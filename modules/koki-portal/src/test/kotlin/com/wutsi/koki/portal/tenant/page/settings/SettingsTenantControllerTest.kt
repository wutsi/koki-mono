package com.wutsi.koki.portal.tenant.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsTenantControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/tenant")
        assertCurrentPageIs(PageName.TENANT_SETTINGS)
    }

    @Test
    fun about() {
        navigateTo("/settings/tenant")
        click(".btn-about")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_ABOUT)
    }

    @Test
    fun types() {
        navigateTo("/settings/tenant")
        click(".btn-type")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_LIST)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/tenant")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission tenant-admin`() {
        setUpUserWithoutPermissions(listOf("tenant:admin"))

        navigateTo("/settings/tenant")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
