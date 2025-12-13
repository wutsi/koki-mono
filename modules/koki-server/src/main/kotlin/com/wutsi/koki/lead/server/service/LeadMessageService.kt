package com.wutsi.koki.lead.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.server.dao.LeadMessageRepository
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class LeadMessageService(
    private val dao: LeadMessageRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): LeadMessageEntity {
        val message = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND)) }

        if (message.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND))
        }
        return message
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        leadIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<LeadMessageEntity> {
        val jql = StringBuilder("SELECT L FROM LeadMessageEntity L  WHERE L.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (leadIds.isNotEmpty()) {
            jql.append(" AND L.lead.id IN :leadIds")
        }
        jql.append(" ORDER BY L.id DESC")
        val query = em.createQuery(jql.toString(), LeadMessageEntity::class.java)
            .setParameter("tenantId", tenantId)
            .setFirstResult(offset)
            .setMaxResults(limit)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (leadIds.isNotEmpty()) {
            query.setParameter("leadIds", leadIds)
        }
        return query.resultList
    }

    @Transactional
    fun create(request: CreateLeadRequest, lead: LeadEntity): LeadMessageEntity {
        return dao.save(
            LeadMessageEntity(
                tenantId = lead.tenantId,
                lead = lead,
                content = request.message,
                visitRequestedAt = request.visitRequestedAt,
                createdAt = Date(),
            )
        )
    }

    fun getMessageRank(message: LeadMessageEntity): Long {
        return dao.countByLeadAndIdIsLessThanEqual(message.lead, message.id ?: -1) ?: 0
    }

    fun countByLead(lead: LeadEntity): Long {
        return dao.countByLead(lead) ?: 0
    }
}
