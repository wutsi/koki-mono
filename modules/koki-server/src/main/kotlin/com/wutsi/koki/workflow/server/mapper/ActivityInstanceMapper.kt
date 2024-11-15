package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.server.domain.ActivityInstance
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.stereotype.Service

@Service
class ActivityInstanceMapper {
    fun toActivityInstance(entity: ActivityInstanceEntity): ActivityInstance {
        return ActivityInstance(
            id = entity.id!!,
            instanceId = entity.workflowInstanceId,
            activityId = entity.activityId,
            assigneeUserId = entity.assigneeId,
            approverUserId = entity.approverId,
            status = entity.status,
            approval = entity.approval,
            approvedAt = entity.approvedAt,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
        )
    }
}
