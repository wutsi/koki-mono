package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceMapper {
    fun toActivityInstanceModel(
        entity: ActivityInstanceSummary,
        activity: ActivityModel,
        assignee: UserModel?,
    ): ActivityInstanceModel {
        return ActivityInstanceModel(
            id = entity.id,
            activity = activity,
            assignee = assignee,
            status = entity.status,
            approval = entity.approval,
            createdAt = entity.createdAt,
            approvedAt = entity.approvedAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
        )
    }
}
