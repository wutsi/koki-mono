package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.lead.dto.SearchLeadResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/SearchLeadEndpoint.sql"])
class SearchLeadEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/leads?id=101&id=100", SearchLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.leads.map { lead -> lead.id }.sorted()
        assertEquals(listOf(100L, 101L), ids)
    }

    @Test
    fun `by listing`() {
        val response = rest.getForEntity("/v1/leads?listing-id=222", SearchLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.leads.map { lead -> lead.id }.sorted()
        assertEquals(listOf(200L, 201L), ids)
    }

    @Test
    fun `by agent`() {
        val response = rest.getForEntity("/v1/leads?agent-user-id=3", SearchLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.leads.map { lead -> lead.id }.sorted()
        assertEquals(listOf(300L), ids)
    }

    @Test
    fun `by status`() {
        val response =
            rest.getForEntity("/v1/leads?status=CONTACTED&status=VISIT_SET", SearchLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.leads.map { lead -> lead.id }.sorted()
        assertEquals(listOf(400L, 401L, 402L), ids)
    }

    @Test
    fun `by user-id`() {
        val response =
            rest.getForEntity("/v1/leads?user-id=1111", SearchLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val ids = response.body!!.leads.map { lead -> lead.id }.sorted()
        assertEquals(listOf(500L), ids)
    }
}
