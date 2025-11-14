package com.wutsi.koki.lead.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.lead.server.dao.LeadRepository
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.listing.server.service.ListingService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class LeadService(
    private val dao: LeadRepository,
    private val listingService: ListingService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): LeadEntity {
        val room = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.LEAD_NOT_FOUND)) }

        if (room.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.LEAD_NOT_FOUND))
        }
        return room
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        listingIds: List<Long> = emptyList(),
        agentUserIds: List<Long> = emptyList(),
        statuses: List<LeadStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<LeadEntity> {
        val jql = StringBuilder("SELECT L FROM LeadEntity L WHERE L.tenantId = :tenantId")

        if (keyword != null) {
            jql.append(" AND ( (UPPER(L.firstName) LIKE :keyword) OR (UPPER(L.lastName) LIKE :keyword) OR (UPPER(L.email) LIKE :keyword) )")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (listingIds.isNotEmpty()) {
            jql.append(" AND L.listing.id IN :listingIds")
        }
        if (agentUserIds.isNotEmpty()) {
            jql.append(" AND L.listing.sellerAgentUserId IN :agentUserIds")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND L.status IN :statuses")
        }
        jql.append(" ORDER BY L.id DESC")

        val query = em.createQuery(jql.toString(), LeadEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword.isNullOrEmpty().not()) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (listingIds.isNotEmpty()) {
            query.setParameter("listingIds", listingIds)
        }
        if (agentUserIds.isNotEmpty()) {
            query.setParameter("agentUserIds", agentUserIds)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateLeadRequest, tenantId: Long): LeadEntity {
        val now = Date()
        return dao.save(
            LeadEntity(
                tenantId = tenantId,
                listing = listingService.get(request.listingId, tenantId),
                status = LeadStatus.NEW,
                source = request.source,
                createdAt = now,
                modifiedAt = now,
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                phoneNumber = request.phoneNumber,
                message = request.message,
                visitRequestedAt = request.visitRequestedAt
            )
        )
    }

    @Transactional
    fun status(id: Long, request: UpdateLeadStatusRequest, tenantId: Long): LeadEntity {
        val lead = get(id, tenantId)
        lead.status = request.status
        lead.nextContactAt = request.nextContactAt
        lead.nextVisitAt = request.nextVisitAt
        lead.modifiedAt = Date()
        return dao.save(lead)
    }
}
