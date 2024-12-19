package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.script.dto.RunScriptRequest
import com.wutsi.koki.script.dto.RunScriptResponse
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.script.server.service.ScriptingEngine
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter
import javax.script.ScriptException

@RestController
@RequestMapping
class RunScriptEndpoint(
    private val service: ScriptService,
    private val engine: ScriptingEngine,
) {
    @PostMapping("/v1/scripts/run")
    fun run(@Valid @RequestBody request: RunScriptRequest): RunScriptResponse {
        val writer = StringWriter()
        try {
            val bindings = engine.eval(
                code = request.code,
                language = request.language,
                input = request.parameters,
                writer = writer,
            )
            return RunScriptResponse(
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
