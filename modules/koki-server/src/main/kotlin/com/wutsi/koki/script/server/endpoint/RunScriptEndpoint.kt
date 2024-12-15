package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.script.dto.ExecuteScriptRequest
import com.wutsi.koki.script.dto.ExecuteScriptResponse
import com.wutsi.koki.script.server.engine.ScriptingEngine
import com.wutsi.koki.script.server.service.ScriptService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter
import javax.script.ScriptException

@RestController
@RequestMapping
class ExecuteScriptEndpoint(
    private val service: ScriptService,
    private val engine: ScriptingEngine,
) {
    @PostMapping("/v1/scripts/{id}/execute")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @Valid @RequestBody request: ExecuteScriptRequest
    ): ExecuteScriptResponse {
        val script = service.get(id, tenantId)
        val writer = StringWriter()
        try {
            val bindings = engine.eval(
                code = script.code,
                language = script.language,
                inputs = request.parameters,
                writer = writer,
            )
            return ExecuteScriptResponse(
                console = writer.toString(),
                bindings = bindings
            )
        } catch (ex: ScriptException) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.SCRIPT_EXECUTION_FAILED,
                    message = "${ex.lineNumber} - ${ex.message}",
                    data = mapOf(
                        "console" to writer.toString()
                    )
                )
            )
        }
    }
}
