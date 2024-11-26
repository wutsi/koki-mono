package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.WorkflowSortBy
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WorkflowService(
    private val dao: WorkflowRepository,
    private val workflowInstanceDao: WorkflowInstanceRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): WorkflowEntity {
        val workflow = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND)) }

        if (workflow.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND))
        }
        return workflow
    }

    fun get(name: String, tenantId: Long): WorkflowEntity {
        return dao.findByNameIgnoreCaseAndTenantId(name, tenantId)
            ?: throw NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND))
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        active: Boolean? = null,
        activityRoleIds: List<Long> = emptyList(),
        approverRoleIds: List<Long> = emptyList(),
        minWorkflowInstanceCount: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: WorkflowSortBy? = null,
        ascending: Boolean = false,
    ): List<WorkflowEntity> {
        val jql = StringBuilder("SELECT W FROM WorkflowEntity AS W ")
        if (activityRoleIds.isNotEmpty()) {
            jql.append(" JOIN W.activities AS A")
        }

        jql.append(" WHERE W.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND W.id IN :ids")
        }
        if (active != null) {
            jql.append(" AND W.active IN :active")
        }
        if (approverRoleIds.isNotEmpty()) {
            jql.append(" AND W.approverRoleId IN :approverRoleIds")
        }
        if (activityRoleIds.isNotEmpty()) {
            jql.append(" AND A.roleId IN :activityRoleIds")
        }
        if (minWorkflowInstanceCount != null) {
            jql.append(" AND A.workflowInstanceCount >= :minWorkflowInstanceCount")
        }
        if (sortBy != null) {
            val column = when (sortBy) {
                WorkflowSortBy.ID -> "id"
                WorkflowSortBy.NAME -> "name"
                WorkflowSortBy.TITLE -> "title"
            }
            jql.append(" ORDER BY W.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), WorkflowEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        if (approverRoleIds.isNotEmpty()) {
            query.setParameter("approverRoleIds", approverRoleIds)
        }
        if (activityRoleIds.isNotEmpty()) {
            query.setParameter("activityRoleIds", activityRoleIds)
        }
        if (minWorkflowInstanceCount != null) {
            query.setParameter("minWorkflowInstanceCount", minWorkflowInstanceCount)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun save(workflow: WorkflowEntity): WorkflowEntity {
        workflow.modifiedAt = Date()
        return dao.save(workflow)
    }

    @Transactional
    fun onCreated(workflowInstance: WorkflowInstanceEntity) {
        val workflow = dao.findById(workflowInstance.workflowId).get()
        workflow.workflowInstanceCount = workflowInstanceDao.countByWorkflowId(workflowInstance.workflowId) ?: 0
        dao.save(workflow)
    }
}
