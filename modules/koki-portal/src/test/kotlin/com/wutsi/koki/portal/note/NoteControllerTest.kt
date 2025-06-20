package com.wutsi.koki.portal.note

import com.wutsi.koki.NoteFixtures.note
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class NoteControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show - without permission note`() {
        setupUserWithoutPermissions(listOf("note"))

        navigateTo("/notes/${note.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `delete - without permission note-delete`() {
        setupUserWithoutPermissions(listOf("note:delete"))

        navigateTo("/notes/${note.id}/delete")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
