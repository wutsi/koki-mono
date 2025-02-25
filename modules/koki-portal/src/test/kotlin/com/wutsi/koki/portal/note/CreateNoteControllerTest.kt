package com.wutsi.koki.portal.note

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class CreateNoteControllerTest : AbstractPageControllerTest() {
    @Test
    fun `create - without permission note-manage`() {
        setUpUserWithoutPermissions(listOf("note:manage"))

        navigateTo("/notes/create")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
