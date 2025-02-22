package com.wutsi.koki.portal.tenant.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.GetBusinessResponse
import kotlin.test.Test

class SettingsBusinessControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/tenant/business")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_BUSINESS)

        assertElementPresent(".table")
        assertElementNotPresent(".empty")
    }

    @Test
    fun `no business configured`() {
        val ex = createHttpClientErrorException(statusCode = 404, errorCode = ErrorCode.BUSINESS_NOT_FOUND)
        doThrow(ex).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetBusinessResponse::class.java)
            )

        navigateTo("/settings/tenant/business")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_BUSINESS)

        assertElementNotPresent(".table")
        assertElementPresent(".empty")
    }

    @Test
    fun configure() {
        navigateTo("/settings/tenant/business")
        click(".btn-edit")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_BUSINESS_EDIT)
    }

    @Test
    fun back() {
        navigateTo("/settings/tenant/business")
        click(".btn-back")
        assertCurrentPageIs(PageName.TENANT_SETTINGS)
    }

    @Test
    fun `import - without permission tenant-admin`() {
        setUpUserWithoutPermissions(listOf("tenant:admin"))

        navigateTo("/settings/tenant/business")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
