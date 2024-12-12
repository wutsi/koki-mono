package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.script.dto.ExecuteScriptRequest
import com.wutsi.koki.script.dto.ExecuteScriptResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@Sql(value = ["/db/test/clean.sql", "/db/test/script/ExecuteScriptEndpoint.sql"])
class ExecuteScriptEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun execute() {
        val request = ExecuteScriptRequest(
            parameters = mapOf(
                "a" to 11,
                "b" to 1,
            )
        )
        val response = rest.postForEntity("/v1/scripts/100/execute", request, ExecuteScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(10, response.body!!.bindings["return"])
        assertEquals("Hello", response.body!!.console)
    }
}
