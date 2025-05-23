package com.wutsi.koki.portal.error

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import com.wutsi.koki.tenant.dto.TenantStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SuspendedControllerTest : AbstractPageControllerTest() {
    @Test
    fun suspended() {
        doReturn(
            ResponseEntity(
                SearchTenantResponse(
                    tenants = tenants.map { tenant ->
                        tenant.copy(status = TenantStatus.SUSPENDED, portalUrl = "http://localhost:$port")
                    }
                ),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchTenantResponse::class.java)
            )

        navigateTo("/")

        assertCurrentPageIs(PageName.ERROR_SUSPENDED)
    }
}
