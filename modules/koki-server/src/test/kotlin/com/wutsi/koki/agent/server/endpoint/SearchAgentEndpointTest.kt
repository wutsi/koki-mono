package com.wutsi.koki.agent.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.agent.dto.SearchAgentResponse
import jdk.internal.agent.resources.agent
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/agent/SearchAgentEndpoint.sql"])
class SearchAgentEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/agents?id=100&id=101", SearchAgentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val agentIds = response.body!!.agents.map { agent -> agent.id }.sorted()
        assertEquals(listOf(100L, 101L), agentIds)
    }

    @Test
    fun `by userId`() {
        val response = rest.getForEntity("/v1/agents?user-id=11", SearchAgentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val agentIds = response.body!!.agents.map { agent -> agent.id }
        assertEquals(listOf(101L), agentIds)
    }
}
