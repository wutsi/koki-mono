package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.mapper.ActivityInstanceMapper
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping
class SearchActivityInstanceEndpoint(
    private val service: ActivityInstanceService,
    private val mapper: ActivityInstanceMapper,
) {
    @GetMapping("/v1/activity-instances")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceIds: List<String> = emptyList(),
        @RequestParam(required = false, name = "assignee-id") assigneeIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "approver-id") approverIds: List<Long> = emptyList(),
        @RequestParam(required = false) status: WorkflowStatus? = null,
        @RequestParam(required = false) approval: ApprovalStatus? = null,

        @RequestParam(required = false, name = "started-from")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        startedFrom: Date? = null,

        @RequestParam(required = false, name = "started-to")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        startedTo: Date? = null,

        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchActivityInstanceResponse {
        val activities = service.search(
            ids = ids,
            assigneeIds = assigneeIds,
            approverIds = approverIds,
            workflowInstanceIds = workflowInstanceIds,
            status = status,
            approval = approval,
            startedFrom = startedFrom,
            startedTo = startedTo,
            tenantId = tenantId,
            limit = limit,
            offset = offset,
        )
        return SearchActivityInstanceResponse(
            activityInstances = activities.map { activity -> mapper.toActivityInstanceSummary(activity) }
        )
    }
}
