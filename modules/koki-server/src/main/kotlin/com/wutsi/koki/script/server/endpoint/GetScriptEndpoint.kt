package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.script.dto.GetScriptResponse
import com.wutsi.koki.script.server.mapper.ScriptMapper
import com.wutsi.koki.script.server.service.ScriptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetScriptEndpoint(
    private val service: ScriptService,
    private val mapper: ScriptMapper,
) {
    @GetMapping("/v1/scripts/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetScriptResponse {
        val script = service.get(id, tenantId)
        return GetScriptResponse(script = mapper.toScript(script))
    }
}
