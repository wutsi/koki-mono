package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.server.domain.ActivityInstance
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.stereotype.Service

@Service
class ActivityInstanceMapper {
    fun toActivityInstance(entity: ActivityInstanceEntity): ActivityInstance {
        return ActivityInstance(
            id = entity.id!!,
            instanceId = entity.instance.id!!,
            activityId = entity.activity.id!!,
            assigneeUserId = entity.assignee?.id,
            approverUserId = entity.activity.id,
            approval = entity.approval,
            approvedAt = entity.approvedAt,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
        )
    }
}
