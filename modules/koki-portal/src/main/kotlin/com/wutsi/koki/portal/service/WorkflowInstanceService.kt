package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.WorkflowInstanceMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.RoleModel
import com.wutsi.koki.portal.model.WorkflowInstanceModel
import com.wutsi.koki.portal.page.workflow.StartWorkflowForm
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceService(
    private val kokiWorkflow: KokiWorkflow,
    private val kokiWorkflowInstance: KokiWorkflowInstance,
    private val workflowMapper: WorkflowMapper,
    private val workflowInstanceMapper: WorkflowInstanceMapper,
    private val workflowService: WorkflowService,
    private val userService: UserService,
    private val formService: FormService,
    private val currentUserHolder: CurrentUserHolder,
) {
    fun create(form: StartWorkflowForm): String {
        // Create the instance
        val workflowInstanceId = kokiWorkflowInstance.create(
            CreateWorkflowInstanceRequest(
                workflowId = form.workflowId,
                participants = form.participants,
                approverUserId = form.approverUserId,
                startAt = form.startAt,
                dueAt = form.dueAt,
                parameters = form.parameters,
            )
        ).workflowInstanceId

        // Start Now
        if (form.startNow) {
            kokiWorkflowInstance.start(workflowInstanceId)
        }

        return workflowInstanceId
    }

    fun workflow(id: String): WorkflowInstanceModel {
        val workflowInstance = kokiWorkflowInstance.get(id).workflowInstance
        val workflow = workflowService.workflow(workflowInstance.workflowId)

        val userIds = mutableSetOf<Long>()
        userIds.addAll(
            workflowInstance.participants.map { participant -> participant.userId }
        )
        workflowInstance.approverUserId?.let { userId ->
            userIds.add(userId)
        }
        val userMap = userService.users(
            ids = userIds.toList(),
            limit = userIds.size
        ).associateBy { user -> user.id }

        val roleIds = workflowInstance.participants.map { participant -> participant.roleId }.toSet()
        val roleMap = userService.roles(
            ids = roleIds.toList(),
            limit = roleIds.size
        ).associateBy { role -> role.id }

        return workflowInstanceMapper.toWorkflowInstanceModel(
            entity = workflowInstance,
            workflow = workflow,
            imageUrl = kokiWorkflowInstance.imageUrl(id),
            users = userMap,
            roles = roleMap
        )
    }

    fun activity(id: String): ActivityInstanceModel {
        val activityInstance = kokiWorkflowInstance.activity(id).activityInstance
        val activity: Activity = activityInstance.activity

        val userIds = listOf(activityInstance.assigneeUserId, activityInstance.approverUserId)
            .mapNotNull { it }
            .toSet()
        val userMap = if (userIds.isNotEmpty()) {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        } else {
            emptyMap()
        }

        val role: RoleModel? = activity.roleId?.let { roleId -> userService.role(roleId) }
        val form = activity.formId?.let { id ->
            formService.forms(
                ids = listOf(id),
                limit = 1,
                workflowInstanceId = activityInstance.workflowInstance.id,
                activityInstanceId = activityInstance.id,
            ).firstOrNull()
        }

        val workflow = workflowService.workflows(ids = listOf(activity.workflowId), limit = 1).first()
        val workflowInstance = workflowInstanceMapper.toWorkflowInstanceModel(
            entity = activityInstance.workflowInstance,
            workflow = workflow,
            imageUrl = kokiWorkflowInstance.imageUrl(id),
            users = userMap,
        )

        return workflowInstanceMapper.toActivityInstanceModel(
            entity = activityInstance,
            workflowInstance = workflowInstance,
            assignee = activityInstance.assigneeUserId?.let { userId -> userMap[userId] },
            approver = activityInstance.approverUserId?.let { userId -> userMap[userId] },
            activity = workflowMapper.toActivityModel(
                entity = activity,
                role = role,
                form = form,
            ),
        )
    }

    fun myActivities(): List<ActivityInstanceModel> {
        return emptyList()
    }
}
