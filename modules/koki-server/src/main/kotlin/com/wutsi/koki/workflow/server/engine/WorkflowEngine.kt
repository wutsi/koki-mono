package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ApprovalEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ApprovalService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID
import kotlin.collections.flatMap

@Service
class WorkflowEngine(
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityInstanceService: ActivityInstanceService,
    private val approvalService: ApprovalService,
    private val userService: UserService,
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

        val activity = workflowInstance.workflow.activities
            .find { activity -> activity.type == ActivityType.START && activity.active }
        return if (activity == null) {
            LOGGER.debug(">>> ${workflowInstance.id} - No START activity found")
            null
        } else {
            // Update workflow instance status
            workflowInstance.status = WorkflowStatus.RUNNING
            workflowInstance.startedAt = Date()
            workflowInstanceService.save(workflowInstance)

            // Execute the activity
            execute(activity, workflowInstance)
        }
    }

    @Transactional
    fun stop(workflowInstance: WorkflowInstanceEntity) {
        LOGGER.debug(">>> ${workflowInstance.id} - Stopping")

        // Ensure that the workflow is RUNNING
        if (workflowInstance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to workflowInstance.status.name)
                )
            )
        }

        // Ensure there are no activity still RUNNING
        val runningInstances = workflowInstance.activityInstances
            .filter { activityInstance -> activityInstance.status == WorkflowStatus.RUNNING }
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
        workflowInstanceService.save(workflowInstance)
    }

    @Transactional
    fun next(workflowInstance: WorkflowInstanceEntity): List<ActivityInstanceEntity> {
        LOGGER.debug(">>> ${workflowInstance.id} - Run Next Activity")

        ensureRunning(workflowInstance)

        // Get all the activities DONE
        val doneActivityInstances = workflowInstance.activityInstances
            .filter { act -> act.status == WorkflowStatus.DONE }
        if (doneActivityInstances.isEmpty()) {
            LOGGER.debug(">>> ${workflowInstance.id} - No activity is has been done")
            return emptyList()
        }

        // Find all successors
        val successorActivities = findSuccessors(doneActivityInstances, workflowInstance)
        if (LOGGER.isDebugEnabled) {
            val predecessorNames = doneActivityInstances.map { done -> done.activity.name }
            LOGGER.debug(">>> ${workflowInstance.id} - $predecessorNames --> " + successorActivities.map { it.name })
        }

        // Execute all successors
        // For each successor, make sure that all its predecessors are DONE
        return successorActivities
            .filter { successor -> areAllPrecessecorDone(successor, workflowInstance) }
            .mapNotNull { successor -> execute(successor, workflowInstance) }
    }

    @Transactional
    fun done(activityInstance: ActivityInstanceEntity, state: Map<String, String>) {
        LOGGER.debug(">>> ${activityInstance.instance.id} > ${activityInstance.id} - Done")

        ensureRunning(activityInstance)
        ensureNoApprovalPending(activityInstance)

        // Set the workflow state
        state.map { entry ->
            workflowInstanceService.setState(entry.key, entry.value, activityInstance.instance)
        }

        // Update the activity
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
        activityInstanceService.save(activityInstance)

        // Run the next
        if (!activityInstance.activity.requiresApproval) {
            next(activityInstance.instance)
        }
    }

    @Transactional
    fun approve(
        activityInstance: ActivityInstanceEntity,
        status: ApprovalStatus,
        approverUserId: Long,
        comment: String?,
    ): ApprovalEntity {
        LOGGER.debug(">>> ${activityInstance.instance.id} > ${activityInstance.id} - Approve status=$status")

        ensureRunning(activityInstance)
        ensureApprovalPending(activityInstance)

        // Save approval
        val now = Date()
        val tenantId = activityInstance.activity.workflow.tenant.id ?: -1
        val approval = approvalService.save(
            ApprovalEntity(
                activityInstance = activityInstance,
                approver = userService.get(approverUserId, tenantId),
                approvedAt = now,
                status = status,
                comment = comment,
            )
        )

        // Update activity
        activityInstance.approval = approval.status
        activityInstance.approvedAt = approval.approvedAt
        if (status == ApprovalStatus.APPROVED) {
            activityInstance.status = WorkflowStatus.DONE
            activityInstance.doneAt = now
        }
        activityInstanceService.save(activityInstance)

        // Execute Next
        if (status == ApprovalStatus.APPROVED) {
            next(activityInstance.instance)
        }
        return approval
    }

    private fun execute(activity: ActivityEntity, workflowInstance: WorkflowInstanceEntity): ActivityInstanceEntity? {
        val now = Date()
        var activityInstance = workflowInstance.activityInstances
            .find { activityInstance -> activityInstance.activity.id == activity.id }
        if (activityInstance == null) {
            activityInstance = activityInstanceService.save(
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
            workflowInstance.activityInstances.add(activityInstance)

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

    private fun ensureRunning(activityInstance: ActivityInstanceEntity) {
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
        ensureRunning(activityInstance.instance)
    }

    private fun ensureRunning(workflowInstance: WorkflowInstanceEntity) {
        if (workflowInstance.status != WorkflowStatus.RUNNING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf(
                        "status" to workflowInstance.status.name,
                        "scope" to "workflow"
                    )
                )
            )
        }
    }

    private fun ensureNoApprovalPending(activityInstance: ActivityInstanceEntity) {
        if (activityInstance.approval == ApprovalStatus.PENDING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_APPROVAL_PENDING,
                )
            )
        }
    }

    private fun ensureApprovalPending(activityInstance: ActivityInstanceEntity) {
        if (activityInstance.approval != ApprovalStatus.PENDING) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NO_APPROVAL_PENDING,
                )
            )
        }
    }

    private fun findSuccessors(
        activityInstances: List<ActivityInstanceEntity>,
        workflowInstance: WorkflowInstanceEntity
    ): List<ActivityEntity> {
        val activeActivities = workflowInstance.workflow.activities.filter { activity -> activity.active }
        val activityInstanceIds =
            workflowInstance.activityInstances.map { activityInstance -> activityInstance.activity.id }
        return activityInstances
            .flatMap { activityInstance -> findSuccessors(activityInstance.activity, activeActivities) }
            .distinctBy { it.id }
            .filter { successor -> !activityInstanceIds.contains(successor.id) }
    }

    private fun findSuccessors(predecessor: ActivityEntity, activities: List<ActivityEntity>): List<ActivityEntity> {
        return activities.filter { activity ->
            val predecessorIds = activity.predecessors.mapNotNull { pred -> pred.id }
            predecessorIds.contains(predecessor.id)
        }
    }

    private fun areAllPrecessecorDone(activity: ActivityEntity, workflowInstance: WorkflowInstanceEntity): Boolean {
        val predecessorIds = activity.predecessors
            .filter { predecessor -> predecessor.active }
            .map { predecessor -> predecessor.id }

        val doneActivityIds = workflowInstance.activityInstances
            .filter { activityInstance -> activityInstance.status == WorkflowStatus.DONE }
            .map { activityInstance -> activityInstance.activity.id }

        return doneActivityIds.containsAll(predecessorIds)
    }
}
