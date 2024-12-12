package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.script.dto.GetScriptResponse
import com.wutsi.koki.script.dto.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@Sql(value = ["/db/test/clean.sql", "/db/test/script/GetScriptEndpoint.sql"])
class GetScriptEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/scripts/100", GetScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val script = response.body!!.script
        assertEquals("S-100", script.name)
        assertEquals("Sample script", script.title)
        assertEquals("description 100", script.description)
        assertEquals(true, script.active)
        assertEquals(Language.JAVASCRIPT, script.language)
        assertEquals("console.log(a+b)", script.code)
        assertEquals(listOf("a", "b"), script.parameters)
    }

    @Test
    fun `invalid id`() {
        val result = rest.getForEntity("/v1/scripts/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/scripts/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/scripts/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }
}
