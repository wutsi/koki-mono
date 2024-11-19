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
        ids: List<Long>,
        active: Boolean?,
        tenantId: Long,
        limit: Int,
        offset: Int,
        sortBy: WorkflowSortBy?,
        ascending: Boolean,
    ): List<WorkflowEntity> {
        val jql = StringBuilder("SELECT W FROM WorkflowEntity AS W WHERE W.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND W.id IN :ids")
        }
        if (active != null) {
            jql.append(" AND W.active IN :active")
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
