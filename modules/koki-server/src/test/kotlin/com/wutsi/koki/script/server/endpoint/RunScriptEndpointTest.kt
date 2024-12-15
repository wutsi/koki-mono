package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.dto.RunScriptRequest
import com.wutsi.koki.script.dto.RunScriptResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class RunScriptEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun run() {
        val request = RunScriptRequest(
            language = Language.JAVASCRIPT,
            parameters = mapOf(
                "a" to 11,
                "b" to 1,
            ),
            code = """
                console.log("Hello");
                a - b;
            """.trimIndent()
        )
        val response = rest.postForEntity("/v1/scripts/run", request, RunScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(10, response.body!!.bindings["return"])
        assertEquals("Hello\n", response.body!!.console)
    }

    @Test
    fun error() {
        val request = RunScriptRequest(
            language = Language.JAVASCRIPT,
            parameters = mapOf(
                "a" to 11,
                "b" to 1,
            ),
            code = """
                console.log("Hello");
                f();
                a - b;
            """.trimIndent()
        )
        val response = rest.postForEntity("/v1/scripts/run", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)

        assertEquals(ErrorCode.SCRIPT_EXECUTION_FAILED, response.body!!.error.code)
        assertEquals("Hello\n", response.body!!.error.data?.get("console"))
    }
}
