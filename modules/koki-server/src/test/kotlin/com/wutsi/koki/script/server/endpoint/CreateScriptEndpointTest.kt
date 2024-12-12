package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.script.dto.CreateScriptRequest
import com.wutsi.koki.script.dto.CreateScriptResponse
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.dao.ScriptRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@Sql(value = ["/db/test/clean.sql", "/db/test/script/CreateScriptEndpoint.sql"])
class CreateScriptEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ScriptRepository

    val request = CreateScriptRequest(
        name = "SCR-001",
        title = "ID Generator",
        description = "This is the description of the script",
        active = true,
        language = Language.JAVASCRIPT,
        parameters = listOf("a", "b"),
        code = "a+b;"
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/scripts", request, CreateScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.scriptId
        val script = dao.findById(id).get()
        assertEquals(request.name, script.name)
        assertEquals(request.title, script.title)
        assertEquals(request.description, script.description)
        assertEquals(request.active, script.active)
        assertEquals(request.language, script.language)
        assertEquals(request.code, script.code)
        assertEquals("a,b", script.parameters)
        assertEquals(false, script.deleted)
    }

    @Test
    fun duplicate() {
        val result = rest.postForEntity("/v1/scripts", request.copy(name = "S-100"), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_DUPLICATE_NAME, result.body!!.error.code)
    }

    @Test
    fun `syntax error`() {
        val result = rest.postForEntity("/v1/scripts", request.copy(code = "???"), ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_COMPILATION_FAILED, result.body!!.error.code)
    }
}
