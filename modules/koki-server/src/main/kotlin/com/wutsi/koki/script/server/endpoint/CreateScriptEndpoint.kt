package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.script.dto.CreateScriptRequest
import com.wutsi.koki.script.dto.CreateScriptResponse
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.script.server.validation.ScriptValidator
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateScriptEndpoint(
    private val service: ScriptService,
    private val validator: ScriptValidator,
) {
    @PostMapping("/v1/scripts")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateScriptRequest
    ): CreateScriptResponse {
        validator.validate(request.code, request.language)

        val script = service.create(request, tenantId)
        return CreateScriptResponse(scriptId = script.id!!)
    }
}
