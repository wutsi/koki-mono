package com.wutsi.koki.agent.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/agent/GetUserAgentEndpoint.sql"])
class GetUserAgentEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/users/11/agent", GetAgentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val agent = response.body!!.agent
        assertEquals(11L, agent.userId)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/users/33/agent", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.AGENT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun otherTenant() {
        val response = rest.getForEntity("/v1/agents/22", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.AGENT_NOT_FOUND, response.body?.error?.code)
    }
}
