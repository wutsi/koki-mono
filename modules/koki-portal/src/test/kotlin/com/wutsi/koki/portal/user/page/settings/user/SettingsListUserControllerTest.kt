package com.wutsi.koki.portal.user.page.settings.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsListUserControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/users")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)

        assertElementCount("tr.user", users.size)
    }

    @Test
    fun more() {
        var entries = mutableListOf<UserSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(users[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchUserResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchUserResponse::class.java)
            )

        navigateTo("/settings/users")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
        assertElementCount("tr.user", entries.size)

        scrollToBottom()
        click("#user-load-more a", 1000)
        assertElementCount("tr.user", 2 * entries.size)
    }

    @Test
    fun back() {
        navigateTo("/settings/users")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }

    @Test
    fun create() {
        navigateTo("/settings/users")
        click(".btn-create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_CREATE)
    }

    @Test
    fun edit() {
        navigateTo("/settings/users")
        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)
    }

    @Test
    fun view() {
        navigateTo("/settings/users")
        click(".btn-view")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER)
    }

    @Test
    fun `list - without permission security-admin`() {
        setUpUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/users")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
