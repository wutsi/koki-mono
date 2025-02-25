package com.wutsi.koki.portal.file.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FileUploadControllerTest : AbstractPageControllerTest() {
    @Test
    fun `download - without permission file-manage`() {
        setUpUserWithoutPermissions(listOf("file:manage"))

        navigateTo("/files/upload")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
