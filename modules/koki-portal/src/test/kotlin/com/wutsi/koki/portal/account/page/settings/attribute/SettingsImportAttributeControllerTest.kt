package com.wutsi.koki.portal.account.page.settings.attribute

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpMethod
import java.io.File
import kotlin.test.Test

class SettingsImportAttributeControllerTest : AbstractPageControllerTest() {
    @Test
    fun import() {
        navigateTo("/settings/accounts/attributes/import")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_IMPORT)

        val file = File.createTempFile("foo", ".csv")

        input("input[type=file]", file.absolutePath)
        click("button[type=submit]", 1000)

        verify(rest).exchange(
            eq("$sdkBaseUrl/v1/attributes/csv"),
            eq(HttpMethod.POST),
            any(),
            eq(ImportResponse::class.java)
        )
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/accounts/attributes/import")
        click(".btn-back")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_LIST)
    }

    @Test
    fun `without permission account-admin`() {
        setUpUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts/attributes/import")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
