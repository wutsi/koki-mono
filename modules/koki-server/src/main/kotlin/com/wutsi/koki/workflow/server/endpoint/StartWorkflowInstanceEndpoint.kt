package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class StartWorkflowInstanceEndpoint(
    private val service: WorkflowInstanceService,
    private val engine: WorkflowEngine,
) {
    @PostMapping("/v1/workflow-instances/{id}/start")
    fun start(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): StartWorkflowInstanceResponse {
        val workflowInstance = service.get(id, tenantId)
        val activity = engine.start(workflowInstance)
        return StartWorkflowInstanceResponse(activity?.id)
    }
}
