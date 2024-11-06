package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.server.mapper.WorkflowMapper
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchWorkflowEndpoint(
    private val service: WorkflowService,
    private val mapper: WorkflowMapper
) {
    @GetMapping("/v1/workflows")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false) id: List<Long> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: String = "id",
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchWorkflowResponse {
        val workflows = service.search(id, active, tenantId, limit, offset, sortBy, ascending)
        return SearchWorkflowResponse(
            workflows = workflows.map { workflow -> mapper.toWorkflowSummary(workflow) }
        )
    }
}
