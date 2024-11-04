package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.RunNextWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RunNextWorkflowInstanceEndpoint(
    private val service: WorkflowInstanceService,
) {
    @PostMapping("/v1/workflow-instances/{id}/run-next")
    fun start(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): RunNextWorkflowInstanceResponse {
        val activities = service.runNext(id, tenantId)
        return RunNextWorkflowInstanceResponse(
            activityInstanceIds = activities.mapNotNull { activity -> activity.id }
        )
    }
}
