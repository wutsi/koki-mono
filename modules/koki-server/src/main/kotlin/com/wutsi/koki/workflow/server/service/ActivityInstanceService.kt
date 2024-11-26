package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.SetActivityInstanceApproverRequest
import com.wutsi.koki.workflow.dto.SetActivityInstanceAssigneeRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ActivityInstanceService(
    private val dao: ActivityInstanceRepository,
    private val participantDao: ParticipantRepository,
    private val activityService: ActivityService,
    private val workflowService: WorkflowService,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): ActivityInstanceEntity {
        val activityInstance = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND)) }

        if (activityInstance.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND))
        }
        return activityInstance
    }

    fun search(
        tenantId: Long,
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
    ): List<ActivityInstanceEntity> {
        val jql = StringBuilder("SELECT A FROM ActivityInstanceEntity A WHERE A.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (workflowInstanceIds.isNotEmpty()) {
            jql.append(" AND A.workflowInstanceId IN :workflowInstanceIds")
        }
        if (assigneeIds.isNotEmpty()) {
            if (isIdNull(assigneeIds)) {
                jql.append(" AND A.assigneeId IS NULL")
            } else {
                jql.append(" AND A.assigneeId IN :assigneeIds")
            }
        }
        if (approverIds.isNotEmpty()) {
            if (isIdNull(approverIds)) {
                jql.append(" AND A.approverId IS NULL")
            } else {
                jql.append(" AND A.approverId IN :approverIds")
            }
        }
        if (status != null) {
            jql.append(" AND A.status IN :status")
        }
        if (approval != null) {
            jql.append(" AND A.approval IN :approval")
        }
        if (startedFrom != null) {
            jql.append(" AND A.startedAt >= :startedFrom")
        }
        if (startedTo != null) {
            jql.append(" AND A.startedAt <= :startedTo")
        }
        jql.append(" ORDER BY A.createdAt DESC")

        val query = em.createQuery(jql.toString(), ActivityInstanceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (workflowInstanceIds.isNotEmpty()) {
            query.setParameter("workflowInstanceIds", workflowInstanceIds)
        }
        if (assigneeIds.isNotEmpty() && !isIdNull(assigneeIds)) {
            query.setParameter("assigneeIds", assigneeIds)
        }
        if (approverIds.isNotEmpty() && !isIdNull(approverIds)) {
            query.setParameter("approverIds", approverIds)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (approval != null) {
            query.setParameter("approval", approval)
        }
        if (startedFrom != null) {
            query.setParameter("startedFrom", startedFrom)
        }
        if (startedTo != null) {
            query.setParameter("startedTo", startedTo)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun save(activityInstance: ActivityInstanceEntity): ActivityInstanceEntity {
        return dao.save(activityInstance)
    }

    @Transactional
    fun setAssignee(request: SetActivityInstanceAssigneeRequest, tenantId: Long): List<ActivityInstanceEntity> {
        // Set Assignee
        val activityInstances = dao.findByIdInAndTenantId(request.activityInstanceIds, tenantId)
        if (!activityInstances.isEmpty()) {
            activityInstances.forEach { activityInstance -> activityInstance.assigneeId = request.userId }
            dao.saveAll(activityInstances)
        }

        // Update participants
        activityInstances.forEach { activityInstance ->
            val activity = activityService.get(activityInstance.activityId)
            val roleId = activity.roleId
            if (roleId != null) {
                val participant = participantDao.findByWorkflowInstanceIdAndRoleId(
                    activityInstance.workflowInstanceId,
                    roleId
                )
                if (participant == null) {
                    participantDao.save(
                        ParticipantEntity(
                            workflowInstanceId = activityInstance.workflowInstanceId,
                            roleId = roleId,
                            userId = request.userId,
                        )
                    )
                } else {
                    participant.userId = request.userId
                    participantDao.save(participant)
                }
            }
        }
        return activityInstances
    }

    @Transactional
    fun setApprover(request: SetActivityInstanceApproverRequest, tenantId: Long): List<ActivityInstanceEntity> {
        val activityInstances = dao.findByIdInAndTenantId(request.activityInstanceIds, tenantId)
        if (!activityInstances.isEmpty()) {
            activityInstances.forEach { activityInstance -> activityInstance.approverId = request.userId }
            dao.saveAll(activityInstances)
        }
        return activityInstances
    }

    private fun isIdNull(values: List<Long>): Boolean {
        return values.size == 1 && values[0] == -1L
    }
}
