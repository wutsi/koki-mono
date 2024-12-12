package com.wutsi.koki.script.server.validation

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.engine.ScriptingEngine
import org.springframework.stereotype.Service
import javax.script.ScriptException

@Service
class ScriptValidator(private val engine: ScriptingEngine) {
    fun validate(code: String, language: Language) {
        try {
            engine.compile(code, language)
        } catch (ex: ScriptException) {
            throw BadRequestException(
                error = Error(
                    code = ErrorCode.SCRIPT_COMPILATION_FAILED,
                    message = "${ex.lineNumber} - ${ex.message}"
                ),
                ex
            )
        }
    }
}
