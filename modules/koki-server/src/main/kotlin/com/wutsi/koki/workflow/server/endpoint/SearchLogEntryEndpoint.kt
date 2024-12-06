package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import com.wutsi.koki.workflow.server.mapper.LogMapper
import com.wutsi.koki.workflow.server.service.LogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchLogEntryEndpoint(
    private val service: LogService,
    private val mapper: LogMapper,
) {
    @GetMapping("/v1/logs")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchLogEntryResponse {
        val logEntries = service.search(
            tenantId = tenantId,
            activityInstanceId = activityInstanceId,
            workflowInstanceId = workflowInstanceId,
            limit = limit,
            offset = offset
        )
        return SearchLogEntryResponse(
            logEntries = logEntries.map { log -> mapper.toLogEntrySummary(log) }
        )
    }
}
