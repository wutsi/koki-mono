package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.CreateNoteResponse
import com.wutsi.koki.note.server.dao.NoteOwnerRepository
import com.wutsi.koki.note.server.dao.NoteRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/note/CreateNoteEndpoint.sql"])
class CreateNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: NoteRepository

    @Autowired
    private lateinit var ownerDao: NoteOwnerRepository

    @Test
    fun create() {
        val request = CreateNoteRequest(
            subject = "New note",
            body = "<p>This is the body of the note</p>",
        )
        val response = rest.postForEntity("/v1/notes", request, CreateNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = response.body!!.noteId
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(USER_ID, note.createdById)
        assertEquals(USER_ID, note.modifiedById)
        assertFalse(note.deleted)
        assertNull(note.deletedAt)
        assertNull(note.deletedById)

        val owners = ownerDao.findByNoteId(noteId)
        assertEquals(0, owners.size)
    }

    @Test
    fun `create and link`() {
        val request = CreateNoteRequest(
            subject = "New note",
            body = "<p>This is the body of the note</p>",
            ownerId = 1111L,
            ownerType = "xxxx"
        )
        val response = rest.postForEntity("/v1/notes", request, CreateNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = response.body!!.noteId
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(USER_ID, note.createdById)
        assertEquals(USER_ID, note.modifiedById)
        assertFalse(note.deleted)
        assertNull(note.deletedAt)
        assertNull(note.deletedById)

        val owners = ownerDao.findByNoteId(noteId)
        assertEquals(1, owners.size)
        assertEquals(request.ownerId, owners[0].ownerId)
        assertEquals(request.ownerType, owners[0].ownerType)
    }
}
