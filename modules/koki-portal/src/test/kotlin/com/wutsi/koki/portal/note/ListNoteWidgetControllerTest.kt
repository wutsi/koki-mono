package com.wutsi.koki.portal.note

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.NoteFixtures.notes
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.UpdateNoteRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class ListNoteWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/notes/widgets/list?test-mode=1")

        assertElementCount(".widget-notes tr.note", notes.size)
        assertElementNotPresent(".empty-note")
    }

    @Test
    fun delete() {
        val id = notes[1].id

        navigateTo("/notes/widgets/list?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        click("#note-$id .btn-delete")
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(kokiNotes).delete(id)
    }

    @Test
    fun edit() {
        navigateTo("/notes/widgets/list?test-mode=1")
        click(".btn-edit", 1000)

        assertElementVisible("#note-modal")
        assertElementVisible("#note-title-edit")
        assertElementNotVisible("#note-title-create")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-note-submit", 1000)

        val request = argumentCaptor<UpdateNoteRequest>()
        verify(kokiNotes).update(any(), request.capture())
        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)

        assertElementNotVisible("#note-modal")
    }

    @Test
    fun `cancel edit`() {
        navigateTo("/notes/widgets/list?test-mode=1")
        click(".btn-edit", 1000)

        click(".btn-cancel", 1000)
        assertElementNotVisible("#note-modal")
    }

    @Test
    fun add() {
        navigateTo("/notes/widgets/list?owner-id=111&owner-type=ACCOUNT&test-mode=1")
        click(".btn-create", 1000)

        assertElementVisible("#note-modal")
        assertElementNotVisible("#note-title-edit")
        assertElementVisible("#note-title-create")
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        click("#btn-note-submit", 1000)

        val request = argumentCaptor<CreateNoteRequest>()
        verify(kokiNotes).create(request.capture())
        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals("ACCOUNT", request.firstValue.ownerType)
        assertEquals(111L, request.firstValue.ownerId)
    }

    @Test
    fun `cancel add`() {
        navigateTo("/notes/widgets/list?owner-id=111&owner-type=ACCOUNT&test-mode=1")
        click(".btn-create", 100)

        click(".btn-cancel")
        assertElementNotVisible("#note-modal")
    }
}
