package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.note.server.dao.NoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/note/DeleteNoteEndpoint.sql"])
class DeleteNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: NoteRepository

    @Test
    fun delete() {
        rest.delete("/v1/notes/100")

        val noteId = 100L
        val note = dao.findById(noteId).get()
        assertTrue(note.deleted)
        assertNotNull(note.deletedAt)
        assertEquals(USER_ID, note.deletedById)
    }
}
