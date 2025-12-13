package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.lead.dto.SearchLeadMessageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/SearchLeadMessageEndpoint.sql"])
class SearchLeadMessageEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/lead-messages?id=1001&id=1011", SearchLeadMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.messages.map { message -> message.id }.sorted()
        assertEquals(listOf(1001L, 1011L), ids)
    }

    @Test
    fun `by lead`() {
        val response = rest.getForEntity("/v1/lead-messages?lead-id=100", SearchLeadMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.messages.map { message -> message.id }.sorted()
        assertEquals(listOf(1001L, 1002L, 1003L), ids)
    }
}
