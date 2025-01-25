package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.CreateNoteResponse
import com.wutsi.koki.note.dto.NoteType
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
            duration = 90,
            type = NoteType.CALL,
            body = """
                <p>
                    <b>Lorem Ipsum</b> is simply dummy text of the printing and typesetting industry.
                    Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
                    when an unknown printer took a galley of type and scrambled it to make a type specimen book.
                    It has survived not only five centuries, but also the leap into electronic typesetting,
                    remaining essentially unchanged.
                    It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                    and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                </p>
                <p>
                    Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...
                </p>
                <p>
                    There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...
                </p>
            """.trimIndent(),
        )
        val response = rest.postForEntity("/v1/notes", request, CreateNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = response.body!!.noteId
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.duration, note.duration)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(
            """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...
            """.trimIndent(),
            note.summary,
        )
        assertEquals(request.type, note.type)
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
            duration = 15,
            type = NoteType.ONLINE_MEETING,
            reference = ObjectReference(id = 1111L, type = ObjectType.TAX),
        )
        val response = rest.postForEntity("/v1/notes", request, CreateNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = response.body!!.noteId
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(request.duration, note.duration)
        assertEquals("This is the body of the note".trimIndent(), note.summary)
        assertEquals(request.type, note.type)
        assertEquals(USER_ID, note.createdById)
        assertEquals(USER_ID, note.modifiedById)
        assertFalse(note.deleted)
        assertNull(note.deletedAt)
        assertNull(note.deletedById)

        val owners = ownerDao.findByNoteId(noteId)
        assertEquals(1, owners.size)
        assertEquals(request.reference!!.id, owners[0].ownerId)
        assertEquals(request.reference!!.type, owners[0].ownerType)
    }
}
