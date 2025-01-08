package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.note.dto.GetNoteResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/note/GetNoteEndpoint.sql"])
class GetNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/notes/100", GetNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val note = response.body!!.note
        assertEquals("Yo", note.subject)
        assertEquals("<p>Man</p>", note.body)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/notes/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/notes/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/notes/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, response.body!!.error.code)
    }
}
