package com.wutsi.koki.note.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.note.dto.NoteType
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.dao.NoteRepository
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/note/UpdateNoteEndpoint.sql"])
class UpdateNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: NoteRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val request = UpdateNoteRequest(
        subject = "New note",
        type = NoteType.CALL,
        duration = 15,
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

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/notes/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val noteId = 100L
        val note = dao.findById(noteId).get()
        assertEquals(TENANT_ID, note.tenantId)
        assertEquals(request.subject, note.subject)
        assertEquals(request.body, note.body)
        assertEquals(
            """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...
            """.trimIndent(),
            note.summary,
        )
        assertEquals(request.type, note.type)
        assertEquals(request.duration, note.duration)
        assertEquals(USER_ID, note.modifiedById)
        assertFalse(note.deleted)
        assertNull(note.deletedAt)
        assertNull(note.deletedById)

        val event = argumentCaptor<NoteUpdatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(note.id, event.firstValue.noteId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }

    @Test
    fun notFound() {
        val response = rest.postForEntity("/v1/notes/999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun deleted() {
        val response = rest.postForEntity("/v1/notes/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun anotherTenant() {
        val response = rest.postForEntity("/v1/notes/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)

        verify(publisher, never()).publish(any())
    }
}
