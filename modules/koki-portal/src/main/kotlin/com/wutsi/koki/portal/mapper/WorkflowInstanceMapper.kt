package com.wutsi.koki.portal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.ParticipantModel
import com.wutsi.koki.portal.model.WorkflowInstanceModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceMapper(
    private val formMapper: FormMapper,
    private val objectMapper: ObjectMapper,
) : TenantAwareMapper() {
    fun toWorkflowInstanceModel(
        entity: WorkflowInstance,
        imageUrl: String,
        workflow: WorkflowModel,
        users: Map<Long, UserModel>,
        roles: Map<Long, RoleModel>,
    ): WorkflowInstanceModel {
        val fmt = createDateFormat()
        val workflowInstance = WorkflowInstanceModel(
            id = entity.id,
            title = entity.title,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            doneAt = entity.doneAt,
            doneAtText = entity.doneAt?.let { date -> fmt.format(date) },
            dueAt = entity.dueAt,
            dueAtText = entity.dueAt?.let { date -> fmt.format(date) },
            startAt = entity.startAt,
            startAtText = fmt.format(entity.startAt),
            startedAt = entity.startedAt,
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            state = entity.state,
            stateJSON = toJSON(entity.state),
            status = entity.status,
            approver = entity.approverUserId?.let { userId -> users[userId] },
            workflow = workflow,
            imageUrl = imageUrl,
            participants = entity.participants.mapNotNull { participant ->
                val user = users[participant.userId]
                val role = roles[participant.roleId]
                if (user != null && role != null) {
                    ParticipantModel(user = user, role = role)
                } else {
                    null
                }
            },
            createdBy = entity.createdById?.let { id -> users[id] }
        )
        workflowInstance.activityInstances = entity.activityInstances
            .map { activityInstance ->
                toActivityInstanceModel(
                    entity = activityInstance,
                    workflowInstance = workflowInstance,
                    activity = toActivityModel(activityInstance, workflow),
                    assignee = activityInstance.assigneeUserId?.let { userId -> users[userId] },
                    approver = activityInstance.approverUserId?.let { userId -> users[userId] },
                )
            }
        return workflowInstance
    }

    private fun toActivityModel(activityInstance: ActivityInstanceSummary, workflow: WorkflowModel): ActivityModel {
        val activity = workflow.activities
            .find { activity -> activity.id == activityInstance.activityId }
            ?: ActivityModel(id = activityInstance.activityId)

        return if (activity.form != null) {
            activity.copy(
                form = activity.form.copy(
                    editUrl = formMapper.toUrl(
                        id = activity.form.id,
                        readOnly = false,
                        preview = false,
                        workflowInstanceId = activityInstance.workflowInstanceId,
                        activityInstanceId = activityInstance.id
                    ),
                    previewUrl = formMapper.toUrl(
                        id = activity.form.id,
                        readOnly = true,
                        preview = true,
                        workflowInstanceId = activityInstance.workflowInstanceId,
                        activityInstanceId = activityInstance.id
                    ),
                )
            )
        } else {
            activity
        }
    }

    fun toWorkflowInstanceModel(
        entity: WorkflowInstanceSummary,
        imageUrl: String,
        workflow: WorkflowModel,
        users: Map<Long, UserModel>,
    ): WorkflowInstanceModel {
        val fmt = createDateFormat()
        val workflowInstance = WorkflowInstanceModel(
            id = entity.id,
            title = entity.title,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            dueAt = entity.dueAt,
            dueAtText = entity.dueAt?.let { date -> fmt.format(date) },
            startAt = entity.startAt,
            startAtText = fmt.format(entity.startAt),
            startedAt = entity.startedAt,
            startedAtText = entity.startedAt?.let { date -> fmt.format(date) },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            status = entity.status,
            approver = entity.approverUserId?.let { userId -> users[userId] },
            workflow = workflow,
            imageUrl = imageUrl,
            doneAt = entity.doneAt,
            doneAtText = entity.doneAt?.let { date -> fmt.format(date) },
            createdBy = entity.createdById?.let { id -> users[id] }
        )
        return workflowInstance
    }

    fun toActivityInstanceModel(
        entity: ActivityInstanceSummary,
        activity: ActivityModel,
        workflowInstance: WorkflowInstanceModel,
        assignee: UserModel?,
        approver: UserModel?
    ): ActivityInstanceModel {
        val fmt = createDateFormat()
        return ActivityInstanceModel(
            id = entity.id,
            activity = activity,
            workflowInstance = workflowInstance,
            assignee = assignee,
            approver = approver,
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
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toActivityInstanceModel(
        entity: ActivityInstance,
        activity: ActivityModel,
        workflowInstance: WorkflowInstanceModel,
        assignee: UserModel?,
        approver: UserModel?,
    ): ActivityInstanceModel {
        val fmt = createDateFormat()
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
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    private fun toJSON(map: Map<String, Any>): String? {
        return if (map.isEmpty()) {
            null
        } else {
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map)
        }
    }
}
