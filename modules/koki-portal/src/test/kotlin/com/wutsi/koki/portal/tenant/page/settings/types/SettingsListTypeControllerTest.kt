package com.wutsi.koki.portal.tenant.page.settings.types

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.SearchTypeResponse
import com.wutsi.koki.tenant.dto.TypeSummary
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsListTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/tenant/types")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_LIST)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<TypeSummary>()
        repeat(20) {
            entries.add(TenantFixtures.types[0].copy())
        }
        doReturn(
            ResponseEntity(
                SearchTypeResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchTypeResponse::class.java)
            )

        navigateTo("/settings/tenant/types")

        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_LIST)
        assertElementCount("tr.type", entries.size)

        scrollToBottom()
        click("#type-load-more a", 1000)
        assertElementCount("tr.type", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/tenant/types")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission tenant-admin`() {
        setUpUserWithoutPermissions(listOf("tenant:admin"))

        navigateTo("/settings/tenant/types")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
