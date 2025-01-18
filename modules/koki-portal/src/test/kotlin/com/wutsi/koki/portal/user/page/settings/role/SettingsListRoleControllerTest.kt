package com.wutsi.koki.portal.user.page.settings

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import kotlin.test.Test

class SettingsListRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/security/roles")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)

        assertElementCount("tr.role", roles.size)
    }

    @Test
    fun more() {
        var entries = mutableListOf<Role>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(roles[0].copy(id = ++seed))
        }
        doReturn(SearchRoleResponse(entries))
            .whenever(kokiUsers)
            .roles(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/security/roles")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
        assertElementCount("tr.role", entries.size)

        scrollToBottom()
        click("#role-load-more a", 1000)
        assertElementCount("tr.role", 2 * entries.size)
    }

    @Test
    fun create() {
        navigateTo("/settings/security/roles")
        click(".btn-create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/security/roles")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }

    @Test
    fun edit() {
        navigateTo("/settings/security/roles")
        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun view() {
        navigateTo("/settings/security/roles")
        click(".btn-view")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }
}
