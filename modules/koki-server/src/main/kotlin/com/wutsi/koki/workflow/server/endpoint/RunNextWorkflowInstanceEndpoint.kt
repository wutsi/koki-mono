package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.RunNextWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RunNextWorkflowInstanceEndpoint(
    private val engine: WorkflowEngine,
) {
    @PostMapping("/v1/workflow-instances/{id}/run-next")
    fun runNext(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): RunNextWorkflowInstanceResponse {
        val activityInstances = engine.next(id, tenantId)
        return RunNextWorkflowInstanceResponse(
            activityInstanceIds = activityInstances.mapNotNull { activityInstance -> activityInstance.id }
        )
    }
}
