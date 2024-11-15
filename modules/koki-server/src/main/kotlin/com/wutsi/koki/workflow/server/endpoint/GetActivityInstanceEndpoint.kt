package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.server.mapper.ActivityInstanceMapper
import com.wutsi.koki.workflow.server.mapper.ActivityMapper
import com.wutsi.koki.workflow.server.mapper.WorkflowInstanceMapper
import com.wutsi.koki.workflow.server.mapper.WorkflowMapper
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetActivityInstanceEndpoint(
    private val service: ActivityInstanceService,
    private val activityService: ActivityService,
    private val workflowService: WorkflowService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val mapper: ActivityInstanceMapper,
    private val activityMapper: ActivityMapper,
    private val workflowMapper: WorkflowMapper,
    private val workflowInstanceMapper: WorkflowInstanceMapper
) {
    @GetMapping("/v1/activity-instances/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetActivityInstanceResponse {
        val activityInstance = service.get(id, tenantId)
        val activity = activityMapper.toActivity(
            activityService.get(activityInstance.activityId)
        )
        val workflow = workflowMapper.toWorkflowSummary(
            workflowService.get(activity.workflowId, tenantId)
        )
        val workflowInstance = workflowInstanceMapper.toWorkflowInstanceSummary(
            workflowInstanceService.get(activityInstance.workflowInstanceId, tenantId)
        )
        return GetActivityInstanceResponse(
            activityInstance = mapper.toActivityInstance(activityInstance, activity, workflowInstance, workflow)
        )
    }
}
