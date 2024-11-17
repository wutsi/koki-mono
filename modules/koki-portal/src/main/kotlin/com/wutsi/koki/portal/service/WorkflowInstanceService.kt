package com.wutsi.koki.portal.rest

import com.wutsi.koki.sdk.KokiActivityInstance
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Service

@Service
class ActivityInstanceService(
    private val currentUserHolder: CurrentUserHolder,
    private val kokiActivityInstance: KokiActivityInstance,
) {
    fun myCurrentActivities(): List<ActivityInstanceSummary> {
        val id = currentUserHolder.id() ?: return emptyList()

        // Activity Instances
        kokiActivityInstance.searchActivities(
            assigneeIds = listOf(id),
            status = WorkflowStatus.RUNNING
        )

        // Activities
        return emptyList()
    }
}
