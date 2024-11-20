package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.UserMapper
import com.wutsi.koki.portal.mapper.WorkflowInstanceMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.WorkflowInstanceModel
import com.wutsi.koki.portal.page.workflow.StartWorkflowForm
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceService(
    private val kokiWorkflow: KokiWorkflow,
    private val kokiWorkflowInstance: KokiWorkflowInstance,
    private val currentUserHolder: CurrentUserHolder,
    private val kokiUser: KokiUser,
    private val workflowMapper: WorkflowMapper,
    private val workflowInstanceMapper: WorkflowInstanceMapper,
    private val userMapper: UserMapper,
    private val workflowService: WorkflowService,
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

    fun workflowInstance(id: String): WorkflowInstanceModel {
        val workflowInstance = kokiWorkflowInstance.workflowInstance(id).workflowInstance
        val workflow = workflowService.workflow(workflowInstance.workflowId)

        val userIds = mutableSetOf<Long>()
        userIds.addAll(
            workflowInstance.participants.map { participant -> participant.userId }
        )
        workflowInstance.approverUserId?.let { userId ->
            userIds.add(userId)
        }
        val userMap = kokiUser.users(
            ids = userIds.toList(),
            limit = userIds.size
        ).users
            .map { user -> userMapper.toUserModel(user) }
            .associateBy { user -> user.id }

        val roleIds = workflowInstance.participants.map { participant -> participant.roleId }.toSet()
        val roleMap = kokiUser.roles(
            ids = roleIds.toList(),
            limit = roleIds.size
        ).roles
            .map { role -> userMapper.toRoleModel(role) }
            .associateBy { role -> role.id }

        return workflowInstanceMapper.toWorkflowInstanceModel(
            entity = workflowInstance,
            workflow = workflow,
            imageUrl = kokiWorkflowInstance.imageUrl(id),
            users = userMap,
            roles = roleMap
        )
    }

    fun myActivities(): List<ActivityInstanceModel> {
        val id = currentUserHolder.id() ?: return emptyList()

        // Activity Instances
        val activityInstances = kokiWorkflowInstance.activities(
            assigneeIds = listOf(id),
            status = WorkflowStatus.RUNNING
        ).activityInstances
        if (activityInstances.isEmpty()) {
            return emptyList()
        }

        // Activities
        val activityIds = activityInstances.map { activityInstance -> activityInstance.activityId }
            .toSet()
            .toList()
        val activityMap = kokiWorkflow.activities(
            ids = activityIds,
            limit = activityIds.size
        )
            .activities
            .map { activity -> workflowMapper.toActivityModel(activity) }
            .associateBy { activity -> activity.id }

        // User
        val me = currentUserHolder.get()
        return activityInstances.map { activityInstance ->
            workflowInstanceMapper.toActivityInstanceModel(
                entity = activityInstance,
                activity = activityMap[activityInstance.activityId]!!,
                assignee = me
            )
        }
    }
}
