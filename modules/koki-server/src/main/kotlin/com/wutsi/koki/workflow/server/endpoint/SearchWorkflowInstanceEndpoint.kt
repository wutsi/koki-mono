package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.mapper.WorkflowInstanceMapper
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping
class SearchWorkflowInstanceEndpoint(
    private val service: WorkflowInstanceService,
    private val mapper: WorkflowInstanceMapper
) {
    @GetMapping("/v1/workflow-instances")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "workflow-id") workflowIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "participant-user-id") participantUserIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "participant-role-id") participantRoleIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "created-by-id") createdById: Long? = null,
        @RequestParam(required = false) status: WorkflowStatus? = null,

        @RequestParam(required = false, name = "start-from")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        startFrom: Date? = null,

        @RequestParam(required = false, name = "start-to")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        startTo: Date? = null,

        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchWorkflowInstanceResponse {
        val workflows = service.search(
            ids = ids,
            workflowIds = workflowIds,
            participantUserIds = participantUserIds,
            participantRoleIds = participantRoleIds,
            createdById = createdById,
            status = status,
            startFrom = startFrom,
            startTo = startTo,
            tenantId = tenantId,
            limit = limit,
            offset = offset,
        )
        return SearchWorkflowInstanceResponse(
            workflowInstances = workflows.map { workflow -> mapper.toWorkflowInstanceSummary(workflow) }
        )
    }
}
