package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.GetLogEntryResponse
import com.wutsi.koki.workflow.server.mapper.LogMapper
import com.wutsi.koki.workflow.server.service.LogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetLogEntryEndpoint(
    private val service: LogService,
    private val mapper: LogMapper,
) {
    @GetMapping("/v1/log/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetLogEntryResponse {
        val entry = service.get(id, tenantId)
        return GetLogEntryResponse(
            logEntry = mapper.toLogEntry(entry)
        )
    }
}
