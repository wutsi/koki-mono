package com.wutsi.koki.lead.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.lead.server.dao.LeadRepository
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.lang.Math.random
import java.util.Date
import java.util.UUID

@Service
class LeadService(
    private val dao: LeadRepository,
    private val listingService: ListingService,
    private val userService: UserService,
    private val leadMessageService: LeadMessageService,
    private val em: EntityManager,
) {
    fun countByListingIdAndTenantId(listingId: Long, tenantId: Long): Long {
        return dao.countByListingIdAndTenantId(listingId, tenantId) ?: 0
    }

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
        userId: Long? = null,
        listingIds: List<Long> = emptyList(),
        agentUserIds: List<Long> = emptyList(),
        statuses: List<LeadStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<LeadEntity> {
        val jql = StringBuilder("SELECT L FROM LeadEntity L ")
        if (keyword != null) {
            jql.append(" JOIN UserEntity U ON L.userId = U.id ")
        }

        jql.append(" WHERE L.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND ( (UPPER(U.displayName) LIKE :keyword) OR (UPPER(U.email) LIKE :keyword) )")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (listingIds.isNotEmpty()) {
            jql.append(" AND L.listing.id IN :listingIds")
        }
        if (agentUserIds.isNotEmpty()) {
            jql.append(" AND L.agentUserId IN :agentUserIds")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND L.status IN :statuses")
        }
        if (userId != null) {
            jql.append(" AND L.userId = :userId")
        }
        jql.append(" ORDER BY L.modifiedAt DESC")

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
        if (userId != null) {
            query.setParameter("userId", userId)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateLeadRequest, tenantId: Long, deviceId: String? = null): LeadEntity {
        val userId = findByUserIdOrCreate(request, tenantId, deviceId)
        var lead = findLead(request, userId, tenantId)
        val now = Date()
        val listing = request.listingId?.let { id -> listingService.get(id, tenantId) }
        val agentUserId = (listing?.sellerAgentUserId ?: request.agentUserId)!!

        if (lead == null) {
            lead = dao.save(
                LeadEntity(
                    tenantId = tenantId,
                    deviceId = deviceId,
                    userId = userId,
                    listing = listing,
                    agentUserId = agentUserId,
                    status = LeadStatus.NEW,
                    source = request.source,
                    createdAt = now,
                    modifiedAt = now,
                )
            )
        }

        lead.lastMessage = leadMessageService.create(request, lead)
        lead.modifiedAt = now
        dao.save(lead)
        return lead
    }

    @Transactional
    fun save(lead: LeadEntity): LeadEntity {
        lead.modifiedAt = Date()
        return dao.save(lead)
    }

    fun findLead(request: CreateLeadRequest, userId: Long, tenantId: Long): LeadEntity? {
        if (request.listingId != null) {
            return dao.findByListingIdAndUserIdAndTenantId(request.listingId!!, userId, tenantId)
        } else if (request.agentUserId != null) {
            return dao.findByListingIdAndUserIdAndAgentUserIdAndTenantId(null, userId, request.agentUserId!!, tenantId)
        } else {
            throw BadRequestException(
                error = Error(ErrorCode.LEAD_LISTING_OR_AGENT_MISSING)
            )
        }
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

    private fun findByUserIdOrCreate(request: CreateLeadRequest, tenantId: Long, deviceId: String?): Long {
        if (request.userId != null) {
            return request.userId!!
        }

        try {
            return userService.getByEmail(request.email, tenantId).id!!
        } catch (ex: NotFoundException) {
            val request = CreateUserRequest(
                username = generateUsername(request.email, tenantId),
                email = request.email,
                displayName = "${request.firstName} ${request.lastName}".trim(),
                password = UUID.randomUUID().toString(),
                mobile = request.phoneNumber,
                country = request.country,
                cityId = request.cityId,
            )
            return userService.create(request, tenantId, deviceId).id!!
        }
    }

    private fun generateUsername(email: String, tenantId: Long): String {
        val i = email.indexOf('@')
        var username = if (i > 0) {
            email.substring(0, i).take(45)
        } else {
            email.take(45)
        }
        for (i in 0..10) {
            if (userService.getByUsernameOrNull(username, tenantId) != null) {
                val suffix = (i + 1) + (random() * 8999).toInt()
                username = "$username$suffix"
            } else {
                return username
            }
        }
        return UUID.randomUUID().toString()
    }
}
