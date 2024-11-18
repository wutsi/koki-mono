package com.wutsi.koki.sdk

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.Date

class KokiWorkflowInstance(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val ACTIVITY_PATH_PREFIX = "/v1/activity-instances"
        private val WORKFLOW_PATH_PREFIX = "/v1/workflow-instances"
    }

    fun activityInstances(
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
