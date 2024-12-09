package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.script.server.service.ScriptService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class DeleteScriptEndpoint(private val service: ScriptService) {
    @DeleteMapping("/v1/scripts/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ) {
        service.delete(id, tenantId)
    }
}
