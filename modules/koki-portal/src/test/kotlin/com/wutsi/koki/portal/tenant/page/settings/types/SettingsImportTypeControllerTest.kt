package com.wutsi.koki.portal.tenant.page.settings.types

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpMethod
import java.io.File
import kotlin.test.Test

class SettingsImportTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun import() {
        navigateTo("/settings/tenant/types/import")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_IMPORT)

        val file = File.createTempFile("foo", ".csv")

        select("#objectType", 1)
        input("input[type=file]", file.absolutePath)
        click("button[type=submit]", 1000)

        verify(rest).exchange(
            eq("$sdkBaseUrl/v1/types/csv?object-type=ACCOUNT"),
            eq(HttpMethod.POST),
            any(),
            eq(ImportResponse::class.java)
        )
        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/tenant/types/import")
        click(".btn-back")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_TYPE_LIST)
    }

    @Test
    fun `import - without permission tenant-admin`() {
        setupUserWithoutPermissions(listOf("tenant:admin"))

        navigateTo("/settings/tenant/types/import")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
