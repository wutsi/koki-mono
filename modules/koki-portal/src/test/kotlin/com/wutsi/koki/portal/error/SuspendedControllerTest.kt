package com.wutsi.koki.portal.error

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import com.wutsi.koki.tenant.dto.TenantStatus
import kotlin.test.Test

class SuspendedControllerTest : AbstractPageControllerTest() {
    @Test
    fun suspended() {
        val tenant = tenants[0].copy(status = TenantStatus.SUSPENDED)
        doReturn(SearchTenantResponse(listOf(tenant))).whenever(kokiTenants).tenants()

        navigateTo("/")

        assertCurrentPageIs(PageName.ERROR_SUSPENDED)
    }
}
