package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.WorkflowSortBy
import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WorkflowService(
    private val dao: WorkflowRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): WorkflowEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND)) }

        if (user.tenant.id != tenantId) {
            throw NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND))
        }
        return user
    }

    fun search(
        ids: List<Long>,
        active: Boolean? = null,
        tenantId: Long,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: WorkflowSortBy? = null,
        ascending: Boolean = false,
    ): List<WorkflowEntity> {
        val jql = StringBuilder("SELECT W FROM WorkflowEntity AS W WHERE W.tenant.id = :tenantId")
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
}
