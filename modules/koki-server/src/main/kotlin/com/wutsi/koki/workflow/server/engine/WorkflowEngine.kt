package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ActivityRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.service.ActivityService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID
import kotlin.collections.flatMap

@Service
class WorkflowEngine(
    private val activityRepository: ActivityRepository,
    private val workflowInstanceDao: WorkflowInstanceRepository,
    private val activityInstanceDao: ActivityInstanceRepository,
    private val activityService: ActivityService,
    private val executorProvider: ActivityExecutorProvider,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEngine::class.java)
    }

    @Transactional
    fun start(workflowInstance: WorkflowInstanceEntity): ActivityInstanceEntity? {
        LOGGER.debug(">>> ${workflowInstance.id} - Starting")

        if (workflowInstance.status != WorkflowStatus.NEW) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to workflowInstance.status.name)
                )
            )
        }

        val activities =
            activityRepository.findByTypeAndActiveAndWorkflow(ActivityType.START, true, workflowInstance.workflow)
        return if (activities.isEmpty()) {
            LOGGER.debug(">>> ${workflowInstance.id} - No START activity found")
            null
        } else {
            // Update workflow instance status
            workflowInstance.status = WorkflowStatus.RUNNING
            workflowInstance.startedAt = Date()
            workflowInstanceDao.save(workflowInstance)

            // Execute the activity
            execute(activities.first(), workflowInstance)
        }
    }

    @Transactional
    fun stop(workflowInstance: WorkflowInstanceEntity) {
        LOGGER.debug(">>> ${workflowInstance.id} - Stopping")

        // Ensure that the workflow is not RUNNING
        if (workflowInstance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to workflowInstance.status.name)
                )
            )
        }

        // Ensure there are no activity RUNNING
        val runningInstances = activityInstanceDao.findByStatusAndInstance(WorkflowStatus.RUNNING, workflowInstance)
        if (runningInstances.isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_STILL_RUNNING,
                    data = mapOf("activityInstanceIds" to runningInstances.mapNotNull { it.id }.joinToString(","))
                )
            )
        }

        // Update workflowInstance status
        workflowInstance.status = WorkflowStatus.DONE
        workflowInstance.doneAt = Date()
        workflowInstanceDao.save(workflowInstance)
    }

    @Transactional
    fun next(workflowInstance: WorkflowInstanceEntity): List<ActivityInstanceEntity> {
        LOGGER.debug(">>> ${workflowInstance.id} - Run Next Activity")

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
            LOGGER.debug(">>> ${workflowInstance.id} - No activity is has been done")
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
            LOGGER.debug(">>> ${workflowInstance.id} - $precedessorNames --> " + successorActivities.map { it.name })
        }

        // Execute all successors
        return successorActivities.mapNotNull { successor -> execute(successor, workflowInstance) }
    }

    @Transactional
    fun done(activityInstance: ActivityInstanceEntity) {
        LOGGER.debug(">>> ${activityInstance.instance.id} > ${activityInstance.id} - Done")

        if (activityInstance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf(
                        "status" to activityInstance.status.name,
                        "scope" to "activity"
                    )
                )
            )
        }
        if (activityInstance.instance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf(
                        "status" to activityInstance.instance.status.name,
                        "scope" to "workflow"
                    )
                )
            )
        }

        if (activityInstance.activity.requiresApproval) {
            LOGGER.debug(">>> ${activityInstance.instance.id} > ${activityInstance.id} - Starting approval...")

            activityInstance.approver = activityInstance.instance.approver
            activityInstance.approval = ApprovalStatus.PENDING
            activityInstance.status = WorkflowStatus.RUNNING
            activityInstance.doneAt = null
        } else {
            activityInstance.status = WorkflowStatus.DONE
            activityInstance.doneAt = Date()
        }
        activityInstanceDao.save(activityInstance)

        // Run the next
        if (!activityInstance.activity.requiresApproval) {
            next(activityInstance.instance)
        }
    }

    private fun execute(activity: ActivityEntity, workflowInstance: WorkflowInstanceEntity): ActivityInstanceEntity? {
        val now = Date()
        var activityInstance = activityInstanceDao.findByActivityAndInstance(activity, workflowInstance)
        if (activityInstance == null) {
            activityInstance = activityInstanceDao.save(
                ActivityInstanceEntity(
                    id = UUID.randomUUID().toString(),
                    instance = workflowInstance,
                    activity = activity,
                    status = WorkflowStatus.RUNNING,
                    assignee = findAssignee(activity, workflowInstance),
                    startedAt = now,
                    createdAt = now,
                    approval = ApprovalStatus.UNKNOWN,
                    approver = null,
                )
            )

            val executor = executorProvider.get(activity.type)
            executor.execute(activityInstance, this)
            return activityInstance
        }
        return null
    }

    private fun findAssignee(activity: ActivityEntity, instance: WorkflowInstanceEntity): UserEntity? {
        if (activity.role == null) {
            return null
        }

        val role = activity.role!!
        return instance.participants
            .find { participant -> participant.role.id == role.id }
            ?.user
    }

    private fun findSuccessors(predecessor: ActivityEntity, activities: List<ActivityEntity>): List<ActivityEntity> {
        return activities.filter { activity ->
            val predecessorIds = activity.predecessors.mapNotNull { pred -> pred.id }
            predecessorIds.contains(predecessor.id)
        }
    }
}
