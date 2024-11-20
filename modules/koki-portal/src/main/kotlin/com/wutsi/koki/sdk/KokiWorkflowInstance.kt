package com.wutsi.koki.sdk

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.Date

class KokiWorkflowInstance(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
) {
    companion object {
        private val ACTIVITY_PATH_PREFIX = "/v1/activity-instances"
        private val WORKFLOW_PATH_PREFIX = "/v1/workflow-instances"
    }

    fun imageUrl(id: String): String {
        val tenantId = tenantProvider.id()
        return urlBuilder.build(
            "$WORKFLOW_PATH_PREFIX/images/$tenantId.$id.png",
        )
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

    fun complete(activityInstanceId: String, data: Map<String, Any>) {
        val url = urlBuilder.build("$ACTIVITY_PATH_PREFIX/$activityInstanceId/complete")
        val request = CompleteActivityInstanceRequest(
            state = data
        )
        rest.postForEntity(url, request, Any::class.java)
    }

    fun workflowInstance(id: String): GetWorkflowInstanceResponse {
        val url = urlBuilder.build("$WORKFLOW_PATH_PREFIX/$id")
        return rest.getForEntity(
            url,
            GetWorkflowInstanceResponse::class.java
        ).body
    }

    fun activities(
        ids: List<String> = emptyList(),
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
}
