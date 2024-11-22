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
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
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

        val workflowInstance = WorkflowInstanceModel(
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
        workflowInstance.activityInstances = entity.activityInstances.map { activityInstance ->
            toActivityInstanceModel(
                entity = activityInstance,
                workflowInstance = workflowInstance,
                activity = workflow.activities.find { activity -> activity.id == activityInstance.activityId }
                    ?: ActivityModel(id = activityInstance.activityId),
                assignee = activityInstance.approverUserId?.let { userId -> users[userId] },
            )
        }
        return workflowInstance
    }

    fun toWorkflowInstanceModel(
        entity: WorkflowInstanceSummary,
        imageUrl: String,
        workflow: WorkflowModel,
        users: Map<Long, UserModel>,
    ): WorkflowInstanceModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")

        val workflowInstance = WorkflowInstanceModel(
            id = entity.id,
            createdAt = entity.createdAt,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            startedAt = entity.startedAt,
            status = entity.status,
            approver = entity.approverUserId?.let { userId -> users[userId] },
            workflow = workflow,
            imageUrl = imageUrl,
            createdAtText = fmt.format(entity.createdAt),
            startAtText = fmt.format(entity.startAt),
            dueAtText = entity.dueAt?.let { date -> fmt.format(date) },
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
        )
        return workflowInstance
    }

    fun toActivityInstanceModel(
        entity: ActivityInstanceSummary,
        activity: ActivityModel,
        workflowInstance: WorkflowInstanceModel,
        assignee: UserModel?,
    ): ActivityInstanceModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return ActivityInstanceModel(
            id = entity.id,
            activity = activity,
            workflowInstance = workflowInstance,
            assignee = assignee,
            status = entity.status,
            approval = entity.approval,
            createdAt = entity.createdAt,
            approvedAt = entity.approvedAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
            createdAtText = fmt.format(entity.createdAt),
            approvedAtText = entity.approvedAt?.let { date -> fmt.format(date) },
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
            doneAtText = entity.doneAt?.let { date -> fmt.format(date) },
        )
    }

    fun toActivityInstanceModel(
        entity: ActivityInstance,
        activity: ActivityModel,
        workflowInstance: WorkflowInstanceModel,
        assignee: UserModel?,
        approver: UserModel?,
    ): ActivityInstanceModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return ActivityInstanceModel(
            id = entity.id,
            workflowInstance = workflowInstance,
            activity = activity,
            assignee = assignee,
            status = entity.status,
            approval = entity.approval,
            approver = approver,
            createdAt = entity.createdAt,
            approvedAt = entity.approvedAt,
            startedAt = entity.startedAt,
            doneAt = entity.doneAt,
            createdAtText = fmt.format(entity.createdAt),
            approvedAtText = entity.approvedAt?.let { date -> fmt.format(date) },
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
            doneAtText = entity.doneAt?.let { date -> fmt.format(date) },
        )
    }
}
