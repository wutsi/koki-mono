package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class DoneActivityInstanceEndpoint(
    private val service: WorkflowInstanceService,
    private val activityInstanceService: ActivityInstanceService,
    private val engine: WorkflowEngine,
) {
    @PostMapping("/v1/workflow-instances/{id}/activities/{activityInstanceId}/done")
    fun done(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @PathVariable activityInstanceId: String,
    ) {
        val workflowInstance = service.get(id, tenantId)
        val activityInstance = activityInstanceService.getById(activityInstanceId, workflowInstance)
        engine.done(activityInstance)
    }
}
