package com.wutsi.koki.portal.contact.page.settings.type

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.page.PageName
import org.springframework.http.HttpMethod
import java.io.File
import kotlin.test.Test

class SettingsImportContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun import() {
        navigateTo("/settings/contacts/types/import")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_IMPORT)

        val file = File.createTempFile("foo", ".csv")

        input("input[type=file]", file.absolutePath)
        click("button[type=submit]", 1000)

        verify(rest).exchange(
            eq("$sdkBaseUrl/v1/contact-types/csv"),
            eq(HttpMethod.POST),
            any(),
            eq(ImportResponse::class.java)
        )
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/contacts/types/import")
        click(".btn-back")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_LIST)
    }

    @Test
    fun `import - without permission contact-admin`() {
        setUpUserWithoutPermissions(listOf("contact:admin"))

        navigateTo("/settings/contacts/types/import")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
