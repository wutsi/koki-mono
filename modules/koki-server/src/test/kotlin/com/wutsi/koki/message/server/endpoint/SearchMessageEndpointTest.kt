package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.message.dto.SearchMessageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/SearchMessageEndpoint.sql"])
class SearchMessageEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/messages", SearchMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val messages = response.body!!.messages
        assertEquals(3, messages.size)
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/messages?status=NEW", SearchMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val messages = response.body!!.messages
        assertEquals(2, messages.size)
        assertEquals(listOf(101L, 102L), messages.map { message -> message.id })
    }

    @Test
    fun `by owner`() {
        val response =
            rest.getForEntity("/v1/messages?owner-id=11&owner-type=ACCOUNT", SearchMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val messages = response.body!!.messages
        assertEquals(2, messages.size)
        assertEquals(listOf(100L, 102L), messages.map { message -> message.id })
    }

    @Test
    fun `by id`() {
        val response =
            rest.getForEntity("/v1/messages?id=100&id=102", SearchMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val messages = response.body!!.messages
        assertEquals(2, messages.size)
        assertEquals(listOf(100L, 102L), messages.map { message -> message.id })
    }
}
