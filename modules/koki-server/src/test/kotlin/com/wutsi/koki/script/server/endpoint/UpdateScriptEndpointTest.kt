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

@Sql(value = ["/db/test/clean.sql", "/db/test/script/UpdateScriptEndpoint.sql"])
class UpdateScriptEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ScriptRepository

    val request = CreateScriptRequest(
        name = "SCR-001",
        title = "ID Generator",
        description = "This is the description of the script",
        active = true,
        language = Language.PYTHON,
        parameters = listOf(),
        code = "return a+b"
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/scripts/100", request, CreateScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val script = dao.findById("100").get()
        assertEquals(request.name, script.name)
        assertEquals(request.title, script.title)
        assertEquals(request.description, script.description)
        assertEquals(request.active, script.active)
        assertEquals(request.language, script.language)
        assertEquals(request.code, script.code)
        assertEquals(null, script.parameters)
    }

    @Test
    fun duplicate() {
        val result = rest.postForEntity("/v1/scripts/110", request.copy(name = "S-120"), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_DUPLICATE_NAME, result.body!!.error.code)
    }

    @Test
    fun `invalid id`() {
        val result = rest.postForEntity("/v1/scripts/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.postForEntity("/v1/scripts/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.postForEntity("/v1/scripts/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SCRIPT_NOT_FOUND, result.body!!.error.code)
    }
}
