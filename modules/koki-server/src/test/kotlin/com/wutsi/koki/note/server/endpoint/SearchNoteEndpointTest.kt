package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.note.dto.SearchNoteResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/note/SearchNoteEndpoint.sql"])
class SearchNoteEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/notes", SearchNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val notes = response.body!!.notes
        assertEquals(5, notes.size)
    }

    @Test
    fun `by owner`() {
        val response = rest.getForEntity("/v1/notes?owner-id=11&owner-type=ACCOUNT", SearchNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val notes = response.body!!.notes
        assertEquals(2, notes.size)
    }

    @Test
    fun `another tenant`() {
        val response = rest.getForEntity("/v1/notes?id=200", SearchNoteResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val notes = response.body!!.notes
        assertEquals(0, notes.size)
    }
}
