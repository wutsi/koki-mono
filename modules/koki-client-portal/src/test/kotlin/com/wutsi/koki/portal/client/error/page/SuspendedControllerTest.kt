package com.wutsi.koki.portal.client.error.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.TenantFixtures.tenants
import com.wutsi.koki.portal.client.common.page.PageName
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
                    tenants.map { tenant ->
                        tenant.copy(
                            clientPortalUrl = "http://localhost:$port",
                            status = TenantStatus.SUSPENDED,
                        )
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
