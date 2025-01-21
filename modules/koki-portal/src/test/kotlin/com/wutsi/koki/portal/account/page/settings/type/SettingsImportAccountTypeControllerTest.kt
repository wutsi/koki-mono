package com.wutsi.koki.portal.account.page.settings.type

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.page.PageName
import org.springframework.http.HttpMethod
import java.io.File
import kotlin.test.Test

class SettingsImportAccountTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun import() {
        navigateTo("/settings/accounts/types/import")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_TYPE_IMPORT)

        val file = File.createTempFile("foo", ".csv")

        input("input[type=file]", file.absolutePath)
        click("button[type=submit]", 1000)

        verify(rest).exchange(
            eq("$sdkBaseUrl/v1/account-types/csv"),
            eq(HttpMethod.POST),
            any(),
            eq(ImportResponse::class.java)
        )
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/accounts/types/import")
        click(".btn-back")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_TYPE_LIST)
    }
}
