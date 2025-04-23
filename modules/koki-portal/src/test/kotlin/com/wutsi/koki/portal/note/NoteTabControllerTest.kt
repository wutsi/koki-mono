package com.wutsi.koki.portal.note

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.NoteFixtures.notes
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.CreateNoteResponse
import com.wutsi.koki.note.dto.NoteType
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        assertElementPresent(".btn-create")
        assertElementPresent(".btn-view")
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-delete")

        assertElementCount(".tab-notes .note", notes.size)
        assertElementAttribute("#note-list", "data-owner-id", "111")
        assertElementAttribute("#note-list", "data-owner-type", "ACCOUNT")
    }

    @Test
    fun `list - readOnly`() {
        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT&read-only=true")

        assertElementNotPresent(".btn-create")
        assertElementPresent(".btn-view")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-delete")

        assertElementCount(".tab-notes .note", notes.size)
        assertElementAttribute("#note-list", "data-owner-id", "111")
        assertElementAttribute("#note-list", "data-owner-type", "ACCOUNT")
    }

    @Test
    fun view() {
        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        click(".note .subject", 1000)
        assertElementVisible("#koki-modal")
    }

    @Test
    fun delete() {
        val id = notes[1].id

        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        click("#note-$id .btn-delete", 100)
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/notes/$id")
    }

    @Test
    fun edit() {
        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")
        click(".btn-edit", 1000)

        assertElementVisible("#koki-modal")
        select("#type", 2)
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        select("#durationHours", 1)
        select("#durationMinutes", 10)
        click("#btn-note-submit", 1000)

        val request = argumentCaptor<UpdateNoteRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/notes/${notes[0].id}"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(NoteType.IN_PERSON_MEETING, request.firstValue.type)
        assertEquals(70, request.firstValue.duration)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `cancel edit`() {
        navigateTo("/notes/tab?owner-id=111&owner-type=ACCOUNT&test-mode=1")
        click(".btn-edit", 1000)

        click(".btn-cancel", 1000)
        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun add() {
        navigateTo("/notes/tab?owner-id=111&owner-type=ACCOUNT&test-mode=1")
        click(".btn-create", 1000)

        assertElementVisible("#koki-modal")
        select("#type", 2)
        input("#subject", "Yo man")
        input("#html-editor .ql-editor", "Hello man")
        select("#durationHours", 1)
        select("#durationMinutes", 10)
        click("#btn-note-submit", 1000)

        val request = argumentCaptor<CreateNoteRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/notes"),
            request.capture(),
            eq(CreateNoteResponse::class.java)
        )

        assertEquals("Yo man", request.firstValue.subject)
        assertEquals("<p>Hello man</p>", request.firstValue.body)
        assertEquals(NoteType.IN_PERSON_MEETING, request.firstValue.type)
        assertEquals(70, request.firstValue.duration)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.owner?.type)
        assertEquals(111L, request.firstValue.owner?.id)
    }

    @Test
    fun `cancel add`() {
        navigateTo("/notes/tab?owner-id=111&owner-type=ACCOUNT&test-mode=1")
        click(".btn-create", 100)

        click(".btn-cancel")
        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `list - without permission note-manage`() {
        setUpUserWithoutPermissions(listOf("note:manage"))

        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        assertElementNotPresent(".btn-create")
        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun `list - without permission note-delete`() {
        setUpUserWithoutPermissions(listOf("note:delete"))

        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun `list - without permission note`() {
        setUpUserWithoutPermissions(listOf("note"))

        navigateTo("/notes/tab?test-mode=1&owner-id=111&owner-type=ACCOUNT")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
