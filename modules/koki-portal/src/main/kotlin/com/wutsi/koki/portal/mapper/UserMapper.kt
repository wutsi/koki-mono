package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.ActivitySummary
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceMapper(
    private val workflowMapper: WorkflowMapper
) {
    fun toActivityInstanceModel(
        entity: ActivityInstanceSummary,
        activity: ActivitySummary,
    ): ActivityInstanceModel {
        return ActivityInstanceModel(
            id = entity.id,
            activity = workflowMapper.toActivityModel(activity),
            status = entity.status,
            approval = entity.approval,
            createdAt = entity.createdAt,
            approvedAt = entity.approvedAt,
            startedAt =   entity.startedAt,
            doneAt = entity.doneAt,
        )
    }
}
