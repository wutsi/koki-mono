package com.wutsi.koki.sdk

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.ReceiveExternalEventRequest
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.SetActivityInstanceAssigneeRequest
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.Date

class KokiWorkflowInstances(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
) {
    companion object {
        private const val ACTIVITY_PATH_PREFIX = "/v1/activity-instances"
        private const val WORKFLOW_PATH_PREFIX = "/v1/workflow-instances"
    }

    fun imageUrl(id: String): String {
        val tenantId = tenantProvider.id()
        return urlBuilder.build(
            "$WORKFLOW_PATH_PREFIX/images/$tenantId.$id.png",
        )
    }

    fun assignee(request: SetActivityInstanceAssigneeRequest) {
        val url = urlBuilder.build("$ACTIVITY_PATH_PREFIX/assignee")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun create(request: CreateWorkflowInstanceRequest): CreateWorkflowInstanceResponse {
        val url = urlBuilder.build(WORKFLOW_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateWorkflowInstanceResponse::class.java).body
    }

    fun start(workflowInstanceId: String): StartWorkflowInstanceResponse {
        val url = urlBuilder.build("$WORKFLOW_PATH_PREFIX/$workflowInstanceId/start")
        return rest.postForEntity(
            url,
            emptyMap<String, String>(),
            StartWorkflowInstanceResponse::class.java
        ).body
    }

    fun received(workflowInstanceId: String, request: ReceiveExternalEventRequest) {
        val url = urlBuilder.build("$WORKFLOW_PATH_PREFIX/$workflowInstanceId/events")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun complete(activityInstanceId: String, request: CompleteActivityInstanceRequest) {
        val url = urlBuilder.build("$ACTIVITY_PATH_PREFIX/$activityInstanceId/complete")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun workflow(id: String): GetWorkflowInstanceResponse {
        val url = urlBuilder.build("$WORKFLOW_PATH_PREFIX/$id")
        return rest.getForEntity(
            url,
            GetWorkflowInstanceResponse::class.java
        ).body
    }

    fun activity(id: String): GetActivityInstanceResponse {
        val url = urlBuilder.build("$ACTIVITY_PATH_PREFIX/$id")
        return rest.getForEntity(
            url,
            GetActivityInstanceResponse::class.java
        ).body
    }

    fun activities(
        ids: List<String> = emptyList(),
        activityIds: List<Long> = emptyList(),
        workflowInstanceIds: List<String> = emptyList(),
        assigneeIds: List<Long> = emptyList(),
        approverIds: List<Long> = emptyList(),
        status: WorkflowStatus? = null,
        approval: ApprovalStatus? = null,
        startedFrom: Date? = null,
        startedTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchActivityInstanceResponse {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val url = urlBuilder.build(
            ACTIVITY_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "workflow-instance-id" to workflowInstanceIds,
                "activity-id" to activityIds,
                "assignee-id" to assigneeIds,
                "approver-id" to approverIds,
                "status" to status?.name,
                "approval" to approval?.name,
                "started-from" to startedFrom?.let { date -> fmt.format(date) },
                "started-to" to startedTo?.let { date -> fmt.format(date) },
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchActivityInstanceResponse::class.java).body!!
    }

    fun workflows(
        ids: List<String> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        participantUserIds: List<Long> = emptyList(),
        participantRoleIds: List<Long> = emptyList(),
        createdById: Long? = null,
        status: List<WorkflowStatus> = emptyList(),
        startFrom: Date? = null,
        startTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchWorkflowInstanceResponse {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val url = urlBuilder.build(
            WORKFLOW_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "workflow-id" to workflowIds,
                "participant-user-id" to participantUserIds,
                "participant-role-id" to participantRoleIds,
                "created-by-id" to createdById,
                "status" to status,
                "start-from" to startFrom?.let { date -> fmt.format(date) },
                "start-to" to startTo?.let { date -> fmt.format(date) },
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchWorkflowInstanceResponse::class.java).body!!
    }
}
