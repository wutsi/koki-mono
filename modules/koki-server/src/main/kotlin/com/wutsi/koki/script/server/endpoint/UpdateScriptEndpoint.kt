package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.script.dto.UpdateScriptRequest
import com.wutsi.koki.script.server.service.ScriptService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateScriptEndpoint(private val service: ScriptService) {
    @PostMapping("/v1/scripts/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateScriptRequest
    ) {
        service.update(id, request, tenantId)
    }
}
