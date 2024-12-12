package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ApprovalEntity
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.ApprovalService
import com.wutsi.koki.workflow.server.service.ExpressionEvaluator
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class WorkflowEngineWorker(
    private val workflowService: WorkflowService,
    private val activityService: ActivityService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityInstanceService: ActivityInstanceService,
    private val approvalService: ApprovalService,
    private val expressionEvaluator: ExpressionEvaluator,
    private val formDataService: FormDataService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEngineWorker::class.java)
    }

    @Transactional
    fun start(workflowInstanceId: String, tenantId: Long): ActivityInstanceEntity? {
        val workflowInstance = workflowInstanceService.get(workflowInstanceId, tenantId)
        if (workflowInstance.status != WorkflowStatus.NEW) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR,
                    data = mapOf("status" to workflowInstance.status.name)
                )
            )
        }

        val workflow = workflowService.get(workflowInstance.workflowId, workflowInstance.tenantId)
        val activity = workflow.activities
            .find { activity -> activity.type == ActivityType.START && activity.active }
        if (activity == null) {
            LOGGER.debug(">>> ${workflowInstance.id} - No START activity found")
            return null
        } else {
            // Update workflow instance status
            workflowInstance.status = WorkflowStatus.RUNNING
            workflowInstance.startedAt = Date()
            workflowInstanceService.save(workflowInstance)

            // Return the start node
            return createActivityInstance(activity, workflowInstance)
        }
    }

    @Transactional
    fun done(activityInstanceId: String, state: Map<String, Any>, tenantId: Long): List<ActivityInstanceEntity> {
        val activityInstance = activityInstanceService.get(activityInstanceId, tenantId)
        val workflowInstance = workflowInstanceService.get(activityInstance.workflowInstanceId, tenantId)
        ensureRunning(activityInstance, workflowInstance)
        ensureNoApprovalPending(activityInstance)

        // Update the workflow state
        workflowInstanceService.mergeState(state, workflowInstance)

        // Update the associate form data
        formDataService.search(
            tenantId = workflowInstance.tenantId,
            workflowInstanceIds = listOf(workflowInstance.id!!),
            limit = 1000
        )
            .forEach { formData -> formDataService.merge(formData, state) }

        // Update the activity
        val result = mutableListOf<ActivityInstanceEntity>()
        val activity = activityService.get(activityInstance.activityId)
        if (activity.requiresApproval) {
            activityInstance.approverId = workflowInstance.approverId
            activityInstance.approval = ApprovalStatus.PENDING
            activityInstance.status = WorkflowStatus.RUNNING
            activityInstance.doneAt = null
        } else {
            activityInstance.status = WorkflowStatus.DONE
            activityInstance.doneAt = Date()
        }
        activityInstanceService.save(activityInstance)
        result.add(activityInstance)

        // Next activities
        if (!activity.requiresApproval) {
            val nextActivityInstances = next(workflowInstance.id!!, workflowInstance.tenantId)
            result.addAll(nextActivityInstances)
        }
        return result
    }

    @Transactional
    fun approve(
        activityInstanceId: String,
        status: ApprovalStatus,
        approverUserId: Long,
        comment: String?,
        tenantId: Long
    ): ApprovalEntity {
        val activityInstance = activityInstanceService.get(activityInstanceId, tenantId)
        val workflowInstance = workflowInstanceService.get(
            activityInstance.workflowInstanceId,
            activityInstance.tenantId
        )
        ensureRunning(activityInstance, workflowInstance)
        ensureApprovalPending(activityInstance)

        // Save approval
        val now = Date()
        val approval = approvalService.save(
            ApprovalEntity(
                activityInstanceId = activityInstance.id!!,
                approverId = approverUserId,
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
        return approval
    }

    @Transactional
    fun done(workflowInstanceId: String, tenantId: Long): WorkflowInstanceEntity {
        val workflowInstance = workflowInstanceService.get(workflowInstanceId, tenantId)

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
        return workflowInstanceService.save(workflowInstance)
    }

    @Transactional
    fun next(workflowInstanceId: String, tenantId: Long): List<ActivityInstanceEntity> {
        val workflowInstance = workflowInstanceService.get(workflowInstanceId, tenantId)
        if (workflowInstance.status == WorkflowStatus.DONE) {
            return emptyList()
        }
        ensureRunning(workflowInstance)

        // Get all the activities DONE
        val doneActivityInstances = workflowInstance.activityInstances
            .filter { act -> act.status == WorkflowStatus.DONE }
        if (doneActivityInstances.isEmpty()) {
            LOGGER.debug(">>> ${workflowInstance.id} - No activity is has been done")
            return emptyList()
        }

        // Find all successors
        val workflow = workflowService.get(workflowInstance.workflowId, workflowInstance.tenantId)
        val successorFlows = findSuccessorFlows(doneActivityInstances, workflow)
        if (LOGGER.isDebugEnabled) {
            val fromIds = doneActivityInstances.map { activityInstance -> activityInstance.activityId }
            val from = activityService.getByIds(fromIds).map { flow -> flow.name }
            val to = successorFlows.map { flow -> flow.to.name }
            LOGGER.debug(">>> ${workflowInstance.id} - $from --> " + to)
        }

        // Execute all successors
        return successorFlows
            .filter { flow -> flow.to.active }
            .filter { flow -> noPredecessorsIsRunning(flow.to, workflowInstance, workflow) }
            .filter { flow -> evaluate(flow, workflowInstance) }
            .mapNotNull { flow -> createActivityInstance(flow.to, workflowInstance) }
    }

    private fun evaluate(flow: FlowEntity, workflowInstance: WorkflowInstanceEntity): Boolean {
        if (!flow.expression.isNullOrEmpty()) {
            val result = expressionEvaluator.evaluate(flow, workflowInstance)
            if (LOGGER.isDebugEnabled) {
                LOGGER.warn(">>>  ${workflowInstance.id} - ${flow.from.name} -> ${flow.to.name} : [${flow.expression}] evaluated to '$result'")
            }
            return result
        } else {
            return true
        }
    }

    private fun createActivityInstance(
        activity: ActivityEntity,
        workflowInstance: WorkflowInstanceEntity,
    ): ActivityInstanceEntity? {
        var activityInstance = workflowInstance.activityInstances
            .find { activityInstance -> activityInstance.activityId == activity.id }

        if (activityInstance == null) {
            val now = Date()
            activityInstance = activityInstanceService.save(
                ActivityInstanceEntity(
                    id = UUID.randomUUID().toString(),
                    tenantId = workflowInstance.tenantId,
                    workflowInstanceId = workflowInstance.id!!,
                    activityId = activity.id!!,
                    status = WorkflowStatus.RUNNING,
                    assigneeId = findAssignee(activity, workflowInstance),
                    startedAt = now,
                    createdAt = now,
                    approval = ApprovalStatus.UNKNOWN,
                    approverId = null,
                )
            )
            workflowInstance.activityInstances.add(activityInstance)
            return activityInstance
        }
        return null
    }

    private fun findAssignee(activity: ActivityEntity, instance: WorkflowInstanceEntity): Long? {
        val roleId = activity.roleId
        if (roleId == null) {
            return null
        }

        return instance.participants
            .find { participant -> participant.roleId == roleId }
            ?.userId
    }

    private fun ensureRunning(activityInstance: ActivityInstanceEntity, workflowInstance: WorkflowInstanceEntity) {
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
        ensureRunning(workflowInstance)
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

    private fun findSuccessorFlows(
        activityInstances: List<ActivityInstanceEntity>,
        workflow: WorkflowEntity,
    ): List<FlowEntity> {
        val activityIds = activityInstances.map { activityInstance -> activityInstance.activityId }

        val flows = workflow.flows
            .filter { flow -> activityIds.contains(flow.from.id) }
            .filter { flow -> flow.to.active }
            .filter { flow -> !activityIds.contains(flow.to.id) }

        return flows
    }

    private fun noPredecessorsIsRunning(
        activity: ActivityEntity,
        workflowInstance: WorkflowInstanceEntity,
        workflow: WorkflowEntity,
    ): Boolean {
        val runningActivityIds = workflowInstance.activityInstances
            .filter { activityInstance -> activityInstance.status == WorkflowStatus.RUNNING }
            .map { activityInstance -> activityInstance.activityId }

        val runningPredecessorActivityIds = workflow.flows
            .filter { flow -> flow.to.id == activity.id }
            .map { flow -> flow.from }
            .filter { activity -> activity.active }
            .filter { activity -> runningActivityIds.contains(activity.id) }
            .map { activity -> activity.id }

        return runningPredecessorActivityIds.isEmpty()
    }
}
