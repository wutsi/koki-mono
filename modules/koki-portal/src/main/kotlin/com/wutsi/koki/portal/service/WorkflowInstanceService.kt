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
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.collections.flatMap

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
        workflowInstance.approverUserId?.let { userId -> userIds.add(userId) }
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

    fun workflows(
        ids: List<String> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        participantUserIds: List<Long> = emptyList(),
        participantRoleIds: List<Long> = emptyList(),
        createdById: Long? = null,
        status: WorkflowStatus? = null,
        startFrom: Date? = null,
        startTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<WorkflowInstanceModel> {
        val workflowInstances = kokiWorkflowInstance.searchWorkflows(
            ids = ids,
            workflowIds = workflowIds,
            participantUserIds = participantUserIds,
            participantRoleIds = participantRoleIds,
            createdById = createdById,
            status = status,
            startFrom = startFrom,
            startTo = startTo,
            limit = limit,
            offset = offset,
        ).workflowInstances

        val workflowIds = workflowInstances.map { workflowInstance ->
            workflowInstance.workflowId
        }.toSet()
        val workflowMap = workflowService.workflows(
            ids = workflowIds.toList(),
            limit = workflowIds.size,
        ).associateBy { workflow -> workflow.id }

        val userIds = workflowInstances.mapNotNull { workflowInstance ->
            workflowInstance.approverUserId
        }.toSet()
        val userMap = if (userIds.isNotEmpty()) {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size,
            )
        } else {
            emptyList()
        }.associateBy { user -> user.id }

        return workflowInstances.map { workflowInstance ->
            workflowInstanceMapper.toWorkflowInstanceModel(
                entity = workflowInstance,
                imageUrl = kokiWorkflowInstance.imageUrl(workflowInstance.id),
                users = userMap,
                workflow = workflowMap[workflowInstance.workflowId]!!,
            )
        }
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

    fun activities(
        ids: List<String> = emptyList(),
        workflowInstanceIds: List<String> = emptyList(),
        assigneeIds: List<Long> = emptyList(),
        approverIds: List<Long> = emptyList(),
        status: WorkflowStatus? = null,
        approval: ApprovalStatus? = null,
        startedFrom: Date? = null,
        startedTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ActivityInstanceModel> {
        currentUserHolder.get() ?: return emptyList()

        // Activity Instances
        val activityInstances = kokiWorkflowInstance.searchActivities(
            ids = ids,
            workflowInstanceIds = workflowInstanceIds,
            assigneeIds = assigneeIds,
            approverIds = approverIds,
            status = status,
            approval = approval,
            startedFrom = startedFrom,
            startedTo = startedTo,
            limit = limit,
            offset = offset,
        ).activityInstances
        if (activityInstances.isEmpty()) {
            return emptyList()
        }

        // Users
        val userIds = activityInstances.flatMap { activityInstance ->
            listOf(activityInstance.assigneeUserId, activityInstance.approverUserId)
        }.filterNotNull().toSet()
        val userMap = if (userIds.isNotEmpty()) {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size,
            )
        } else {
            emptyList()
        }.associateBy { user -> user.id }

        // Activities
        val activityIds = activityInstances.map { activityInstance -> activityInstance.activityId }
            .toSet()
        val activityMap = kokiWorkflow.searchActivities(
            ids = activityIds.toList(),
            limit = activityIds.size
        ).activities
            .map { activity -> workflowMapper.toActivityModel(activity) }
            .associateBy { activity -> activity.id }

        // Workflows
        val workflowInstanceIds = activityInstances.map { activityInstance -> activityInstance.workflowInstanceId }
            .toSet()
        val workflowInstanceMap = workflows(
            ids = workflowInstanceIds.toList(),
            limit = workflowInstanceIds.size,
        ).associateBy { workflowInstance -> workflowInstance.id }

        return activityInstances.map { activityInstance ->
            workflowInstanceMapper.toActivityInstanceModel(
                entity = activityInstance,
                activity = activityMap[activityInstance.activityId]!!,
                assignee = activityInstance.assigneeUserId?.let { userId -> userMap[userId] },
                approver = activityInstance.approverUserId?.let { userId -> userMap[userId] },
                workflowInstance = workflowInstanceMap[activityInstance.workflowInstanceId]!!,
            )
        }
    }
}
