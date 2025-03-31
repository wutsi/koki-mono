package com.wutsi.koki.note.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.server.dao.NoteRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/note/DeleteNoteEndpoint.sql"])
class DeleteNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: NoteRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun delete() {
        rest.delete("/v1/notes/100")

        val noteId = 100L
        val note = dao.findById(noteId).get()
        assertTrue(note.deleted)
        assertNotNull(note.deletedAt)
        assertEquals(USER_ID, note.deletedById)

        val event = argumentCaptor<NoteDeletedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(note.id, event.firstValue.noteId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }
}
