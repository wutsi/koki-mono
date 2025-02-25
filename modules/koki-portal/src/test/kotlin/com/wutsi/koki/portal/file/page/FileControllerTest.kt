package com.wutsi.koki.portal.file.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FileControllerTest : AbstractPageControllerTest() {
    @Test
    fun `download - without permission file`() {
        setUpUserWithoutPermissions(listOf("file"))

        navigateTo("/files/${file.id}/download")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
