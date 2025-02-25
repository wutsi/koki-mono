package com.wutsi.koki.portal.note

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.NoteFixtures.note
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class EditNoteControllerTest : AbstractPageControllerTest() {
    @Test
    fun `edit - without permission note-manage`() {
        setUpUserWithoutPermissions(listOf("note:manage"))

        navigateTo("/notes/${note.id}/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
