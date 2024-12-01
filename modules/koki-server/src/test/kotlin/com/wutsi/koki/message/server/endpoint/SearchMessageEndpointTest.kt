package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.message.dto.SearchMessageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/message/SearchMessageEndpoint.sql"])
class SearchMessageEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/messages?sort-by=CREATED_AT", SearchMessageResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val messages = result.body!!.messages
        assertEquals(4, messages.size)
    }

    @Test
    fun `by name`() {
        val result = rest.getForEntity(
            "/v1/messages?name=M-100&sort-by=MODIFIED_AT",
            SearchMessageResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val messages = result.body!!.messages
        assertEquals(1, messages.size)
        assertEquals("m-100", messages[0].name)
        assertEquals("Subject 100", messages[0].subject)
        assertEquals(true, messages[0].active)
    }

    @Test
    fun `by ids`() {
        val result = rest.getForEntity(
            "/v1/messages?id=100&id=110&sort-by=CREATED_AT&asc=false",
            SearchMessageResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val messages = result.body!!.messages
        assertEquals(2, messages.size)
        assertEquals("110", messages[0].id)
        assertEquals("100", messages[1].id)
    }

    @Test
    fun `by active`() {
        val result = rest.getForEntity(
            "/v1/messages?active=false",
            SearchMessageResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val messages = result.body!!.messages
        assertEquals(2, messages.size)
        assertTrue(messages.map { it.id }.containsAll(listOf("120", "130")))
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity(
            "/v1/messages?id=200",
            SearchMessageResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val messages = result.body!!.messages
        assertEquals(0, messages.size)
    }
}
