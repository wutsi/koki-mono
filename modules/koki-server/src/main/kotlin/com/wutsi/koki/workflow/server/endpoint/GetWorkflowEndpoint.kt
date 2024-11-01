package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.server.mapper.WorkflowMapper
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetWorkflowEndpoint(
    private val service: WorkflowService,
    private val mapper: WorkflowMapper,
) {
    @GetMapping("/v1/workflows/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetWorkflowResponse {
        val workflow = service.get(id, tenantId)
        return GetWorkflowResponse(
            workflow = mapper.toWorkflow(workflow)
        )
    }
}
