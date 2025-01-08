package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.note.server.dao.NoteRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/note/UpdateNoteEndpoint.sql"])
class UpdateNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: NoteRepository

    private val request = UpdateNoteRequest(
        subject = "New note",
        body = "<p>This is the body of the note</p>",
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/notes/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = 100L
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(USER_ID, note.modifiedById)
        assertFalse(note.deleted)
        assertNull(note.deletedAt)
        assertNull(note.deletedById)
    }

    @Test
    fun notFound() {
        val response = rest.postForEntity("/v1/notes/999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun deleted() {
        val response = rest.postForEntity("/v1/notes/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.postForEntity("/v1/notes/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }
}
