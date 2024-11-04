package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ParameterRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ParameterEntity
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID
import kotlin.collections.flatMap

@Service
class WorkflowInstanceService(
    private val instanceDao: WorkflowInstanceRepository,
    private val participantDao: ParticipantRepository,
    private val parameterDao: ParameterRepository,
    private val activityInstanceDao: ActivityInstanceRepository,
    private val workflowService: WorkflowService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val activityService: ActivityService,
    private val workflowEngine: WorkflowEngine,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowInstanceService::class.java)
    }

    fun get(id: String, tenantId: Long): WorkflowInstanceEntity {
        val instance = instanceDao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND)) }

        if (instance.tenant.id != tenantId) {
            throw NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND))
        }
        return instance
    }

    @Transactional
    fun create(request: CreateWorkflowInstanceRequest, tenantId: Long): WorkflowInstanceEntity {
        val instance = createInstance(request, tenantId)
        createParticipants(request, instance, tenantId)
        createParameters(request, instance)
        return instance
    }

    private fun createInstance(
        request: CreateWorkflowInstanceRequest,
        tenantId: Long
    ): WorkflowInstanceEntity {
        val workflow = workflowService.get(request.workflowId, tenantId)
        val instance = instanceDao.save(
            WorkflowInstanceEntity(
                id = UUID.randomUUID().toString(),
                tenant = workflow.tenant,
                workflow = workflow,
                status = WorkflowStatus.NEW,
                approver = request.approverUserId?.let { userId -> userService.get(userId, tenantId) },
                startAt = request.startAt,
                dueAt = request.dueAt,
            )
        )
        LOGGER.debug(">>> WorkflowInstance created: ${instance.id}")
        return instance
    }

    private fun createParticipants(
        request: CreateWorkflowInstanceRequest,
        instance: WorkflowInstanceEntity,
        tenantId: Long
    ): List<ParticipantEntity> {
        // Roles
        val roleIds = request.participants.map { participant -> participant.roleId }.toSet()
        if (roleIds.isEmpty()) {
            return emptyList()
        }
        val roleMap = roleService.getAll(roleIds.toList(), tenantId).associateBy { role -> role.id }

        // Users
        val userIds = request.participants.map { participant -> participant.userId }.toSet()
        val userMap = userService.getAll(userIds.toList(), tenantId).associateBy { user -> user.id }

        // Participants
        val participants = request.participants
            .map { participant ->
                val role = roleMap[participant.roleId]!!
                val user = userMap[participant.userId]!!
                LOGGER.debug(">>> ${instance.id} - Adding Participant User[${user.id}] as Role[${role.name}]")
                ParticipantEntity(
                    instance = instance,
                    role = role,
                    user = user
                )
            }

        if (participants.isNotEmpty()) {
            participantDao.saveAll(participants)
        }
        return participants
    }

    private fun createParameters(
        request: CreateWorkflowInstanceRequest,
        instance: WorkflowInstanceEntity,
    ): List<ParameterEntity> {
        val parameters = request.parameters
            .map { entry ->
                LOGGER.debug(">>> ${instance.id} - Adding Parameter[${entry.key}=${entry.value}]")
                ParameterEntity(
                    instance = instance,
                    name = entry.key,
                    value = entry.value
                )
            }

        if (parameters.isNotEmpty()) {
            parameterDao.saveAll(parameters)
        }
        return parameters
    }

    @Transactional
    fun start(instanceId: String, tenantId: Long): ActivityInstanceEntity? {
        // Get instance
        val instance = get(instanceId, tenantId)
        if (instance.status != WorkflowStatus.NEW) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to instance.status.name)
                )
            )
        }

        // Get the activity to execute
        val activities = activityService.getByTypeAndActive(ActivityType.START, true, instance.workflow)
        return if (activities.isEmpty()) {
            LOGGER.debug(">>> $instanceId - No START activity found")
            null
        } else {
            // Update workflow
            instance.status = WorkflowStatus.RUNNING
            instance.startedAt = Date()
            instanceDao.save(instance)

            // Execute the activity
            execute(activities.first(), instance)
        }
    }

    @Transactional
    fun runNext(instanceId: String, tenantId: Long): List<ActivityInstanceEntity> {
        val workflowInstance = get(instanceId, tenantId)
        if (workflowInstance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to workflowInstance.status.name)
                )
            )
        }

        // Get all the activities DONE
        val activityInstances = activityInstanceDao.findByInstance(workflowInstance)
        val doneActivityInstances = activityInstances.filter { act -> act.status == WorkflowStatus.DONE }
        if (doneActivityInstances.isEmpty()) {
            LOGGER.debug(">>> $instanceId - No activity is has been done")
            return emptyList()
        }

        // Find all successors
        val activities = activityService.getByActive(true, workflowInstance.workflow)
        val activityInstanceIds = activityInstances.map { done -> done.activity.id }
        val successorActivities = doneActivityInstances.flatMap { done -> findSuccessors(done.activity, activities) }
            .distinctBy { it.id }
            .filter { successor -> !activityInstanceIds.contains(successor.id) }
        if (LOGGER.isDebugEnabled) {
            val precedessorNames = doneActivityInstances.map { done -> done.activity.name }
            LOGGER.debug(">>> $instanceId - $precedessorNames --> " + successorActivities.map { it.name })
        }

        // Execute all successors
        return successorActivities.mapNotNull { successor -> execute(successor, workflowInstance) }
    }

    private fun findSuccessors(predecessor: ActivityEntity, activities: List<ActivityEntity>): List<ActivityEntity> {
        return activities.filter { activity ->
            val predecessorIds = activity.predecessors.mapNotNull { pred -> pred.id }
            predecessorIds.contains(predecessor.id)
        }
    }

    private fun execute(activity: ActivityEntity, instance: WorkflowInstanceEntity): ActivityInstanceEntity? {
        val now = Date()
        var activityInstance = activityInstanceDao.findByActivityAndInstance(activity, instance)
        if (activityInstance == null) {
            activityInstance = activityInstanceDao.save(
                ActivityInstanceEntity(
                    id = UUID.randomUUID().toString(),
                    instance = instance,
                    activity = activity,
                    status = WorkflowStatus.RUNNING,
                    assignee = findAssignee(activity, instance),
                    startedAt = now,
                    createdAt = now,
                    approval = ApprovalStatus.UNKNOWN,
                    approver = null,
                )
            )

            workflowEngine.execute(activityInstance)
            return activityInstance
        }
        return null
    }

    private fun findAssignee(activity: ActivityEntity, instance: WorkflowInstanceEntity): UserEntity? {
        if (activity.role == null) {
            return null
        }

        val role = activity.role!!
        val assignee = instance.participants
            .find { participant -> participant.role.id == role.id }
            ?.user
        if (assignee == null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_ASSIGNEE_NOT_FOUND,
                    data = mapOf("role" to role.name)
                )
            )
        }
        return assignee
    }
}
