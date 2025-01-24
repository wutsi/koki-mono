package com.wutsi.koki.portal.tax.page.settings.type

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.page.PageName
import org.springframework.http.HttpMethod
import java.io.File
import kotlin.test.Test

class SettingsImportTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun import() {
        navigateTo("/settings/taxes/types/import")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_IMPORT)

        val file = File.createTempFile("foo", ".csv")

        input("input[type=file]", file.absolutePath)
        click("button[type=submit]", 1000)

        verify(rest).exchange(
            eq("$sdkBaseUrl/v1/tax-types/csv"),
            eq(HttpMethod.POST),
            any(),
            eq(ImportResponse::class.java)
        )
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/taxes/types/import")
        click(".btn-back")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_LIST)
    }
}
