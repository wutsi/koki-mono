package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.stereotype.Service

@Service
class ActivityInstanceMapper {
    fun toActivityInstance(
        entity: ActivityInstanceEntity,
        activity: Activity,
        workflowInstance: WorkflowInstanceSummary,
        workflow: WorkflowSummary,
    ): ActivityInstance {
        return ActivityInstance(
            id = entity.id!!,
            workflowInstance = workflowInstance,
            activity = activity,
            workflow = workflow,
            assigneeUserId = entity.assigneeId,
            approverUserId = entity.approverId,
            status = entity.status,
            approval = entity.approval,
            approvedAt = entity.approvedAt,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toActivityInstanceSummary(entity: ActivityInstanceEntity): ActivityInstanceSummary {
        return ActivityInstanceSummary(
            id = entity.id!!,
            workflowInstanceId = entity.workflowInstanceId,
            activityId = entity.activityId,
            assigneeUserId = entity.assigneeId,
            approverUserId = entity.approverId,
            status = entity.status,
            approval = entity.approval,
            approvedAt = entity.approvedAt,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
