package com.wutsi.koki.portal.user.page.settings.role

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsListRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/roles")
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
        doReturn(
            ResponseEntity(
                SearchRoleResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoleResponse::class.java)
            )

        navigateTo("/settings/roles")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
        assertElementCount("tr.role", entries.size)

        scrollToBottom()
        click("#role-load-more a", 1000)
        assertElementCount("tr.role", 2 * entries.size)
    }

    @Test
    fun create() {
        navigateTo("/settings/roles")
        click(".btn-create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/roles")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }

    @Test
    fun edit() {
        navigateTo("/settings/roles")
        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun view() {
        navigateTo("/settings/roles")
        click(".btn-view")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }

    @Test
    fun `list - without permission security-admin`() {
        setUpUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
