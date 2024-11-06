package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.mapper.WorkflowInstanceMapper
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetWorkflowInstanceEndpoint(
    private val service: WorkflowInstanceService,
    private val mapper: WorkflowInstanceMapper,
) {
    @GetMapping("/v1/workflow-instances/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetWorkflowInstanceResponse {
        val workflow = service.get(id, tenantId)
        return GetWorkflowInstanceResponse(
            workflowInstance = mapper.toWorkflowInstance(workflow)
        )
    }
}
