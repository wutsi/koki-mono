package com.wutsi.koki.portal.note

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.NoteFixtures.note
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class NoteControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show - without permission note`() {
        setUpUserWithoutPermissions(listOf("note"))

        navigateTo("/notes/${note.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `delete - without permission note-delete`() {
        setUpUserWithoutPermissions(listOf("note:delete"))

        navigateTo("/notes/${note.id}/delete")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
