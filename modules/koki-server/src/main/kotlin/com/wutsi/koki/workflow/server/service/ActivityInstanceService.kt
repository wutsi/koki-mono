package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ActivityInstanceService(
    private val dao: ActivityInstanceRepository,
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
        ids: List<String>,
        assigneeIds: List<Long>,
        approverIds: List<Long>,
        status: WorkflowStatus?,
        approval: ApprovalStatus?,
        startedFrom: Date?,
        startedTo: Date?,
        tenantId: Long,
        limit: Int,
        offset: Int,
    ): List<ActivityInstanceEntity> {
        val jql = StringBuilder("SELECT A FROM ActivityInstanceEntity A WHERE A.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (assigneeIds.isNotEmpty()) {
            jql.append(" AND A.assigneeId IN :assigneeIds")
        }
        if (approverIds.isNotEmpty()) {
            jql.append(" AND A.approverId IN :approverIds")
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
        if (assigneeIds.isNotEmpty()) {
            query.setParameter("assigneeIds", assigneeIds)
        }
        if (approverIds.isNotEmpty()) {
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
}
