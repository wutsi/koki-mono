package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FileUploadControllerTest : AbstractPageControllerTest() {
    @Test
    fun `download - without permission file-manage`() {
        setupUserWithoutPermissions(listOf("file:manage"))

        navigateTo("/files/upload")

        assertCurrentPageIs(PageName.ERROR_403)
    }
}
