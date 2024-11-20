package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.ParticipantModel
import com.wutsi.koki.portal.model.RoleModel
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.portal.model.WorkflowInstanceModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowInstance
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class WorkflowInstanceMapper {
    fun toWorkflowInstanceModel(
        entity: WorkflowInstance,
        imageUrl: String,
        workflow: WorkflowModel,
        users: Map<Long, UserModel>,
        roles: Map<Long, RoleModel>,
    ): WorkflowInstanceModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")

        return WorkflowInstanceModel(
            id = entity.id,
            createdAt = entity.createdAt,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            startedAt = entity.startedAt,
            state = entity.state,
            parameters = entity.parameters,
            status = entity.status,
            approver = entity.approverUserId?.let { userId -> users[userId] },
            workflow = workflow,
            imageUrl = imageUrl,
            createdAtText = fmt.format(entity.createdAt),
            startAtText = fmt.format(entity.startAt),
            dueAtText = entity.dueAt?.let { date -> fmt.format(date) },
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
            activityInstances = entity.activityInstances.map { activityInstance ->
                toActivityInstanceModel(
                    entity = activityInstance,
                    activity = workflow.activities.find { activity -> activity.id == activityInstance.activityId }
                        ?: ActivityModel(id = activityInstance.activityId),
                    assignee = activityInstance.approverUserId?.let { userId -> users[userId] },
                )
            },
            participants = entity.participants.mapNotNull { participant ->
                val user = users[participant.userId]
                val role = roles[participant.roleId]
                if (user != null && role != null) {
                    ParticipantModel(user = user, role = role)
                } else {
                    null
                }
            }
        )
    }

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
