package com.wutsi.koki.offer.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.offer.server.dao.OfferRepository
import com.wutsi.koki.offer.server.dao.OfferStatusRepository
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.domain.OfferStatusEntity
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class OfferService(
    private val offerDao: OfferRepository,
    private val versionDao: OfferVersionRepository,
    private val statusDao: OfferStatusRepository,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): OfferEntity {
        val user = offerDao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.OFFER_NOT_FOUND)) }

        if (user.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.OFFER_NOT_FOUND))
        }
        return user
    }

    fun countByOwnerIdAndOwnerTypeAndTenantId(ownerId: Long, ownerType: ObjectType, tenantId: Long): Int {
        return offerDao.countByOwnerIdAndOwnerTypeAndTenantId(ownerId, ownerType, tenantId)?.toInt() ?: 0
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        agentUserId: Long? = null,
        assigneeUserId: Long? = null,
        statuses: List<OfferStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<OfferEntity> {
        val jql = StringBuilder("SELECT O FROM OfferEntity O WHERE O.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND O.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND O.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND O.ownerType = :ownerType")
        }
        if (agentUserId != null) {
            jql.append(" AND (O.buyerAgentUserId = :agentUserId OR O.sellerAgentUserId = :agentUserId)")
        }
        if (assigneeUserId != null) {
            jql.append(" AND O.version.assigneeUserId = :assigneeUserId ")
        }
        if (!statuses.isEmpty()) {
            jql.append(" AND O.status IN :statuses")
        }
        jql.append(" ORDER BY O.id DESC")

        val query = em.createQuery(jql.toString(), OfferEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }
        if (agentUserId != null) {
            query.setParameter("agentUserId", agentUserId)
        }
        if (assigneeUserId != null) {
            query.setParameter("assigneeUserId", assigneeUserId)
        }
        if (!statuses.isEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun searchNotExpired(date: Date): List<OfferEntity> {
        return offerDao.findByStatusAndNotExpired(OfferStatus.SUBMITTED, date)
    }

    @Transactional
    fun create(request: CreateOfferRequest, tenantId: Long): OfferEntity {
        // Create offer
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val offer = offerDao.save(
            OfferEntity(
                tenantId = tenantId,
                sellerAgentUserId = request.sellerAgentUserId,
                buyerAgentUserId = request.buyerAgentUserId,
                buyerContactId = request.buyerContactId,
                status = OfferStatus.UNKNOWN,
                ownerType = request.owner?.type,
                ownerId = request.owner?.id,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )

        // Create version
        val version = versionDao.save(
            OfferVersionEntity(
                tenantId = offer.tenantId,
                offer = offer,
                status = offer.status,
                createdById = userId,
                price = request.price,
                currency = request.currency,
                submittingParty = request.submittingParty,
                createdAt = now,
                modifiedAt = now,
                closingAt = request.closingAt,
                expiresAt = request.expiresAt,
                contingencies = request.contingencies?.ifEmpty { null },
                assigneeUserId = resolveAssigneeId(offer, request.submittingParty),
            )
        )

        // Update offer
        offer.version = version
        offer.totalVersions = 1
        offerDao.save(offer)

        // Update status
        return status(offer, UpdateOfferStatusRequest(status = OfferStatus.SUBMITTED))
    }

    @Transactional
    fun create(request: CreateOfferVersionRequest, tenantId: Long): OfferVersionEntity {
        val offer = get(request.offerId, tenantId)
        if (offer.status != OfferStatus.SUBMITTED) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.OFFER_BAD_STATUS,
                    message = "Offer status is ${offer.status}. It should be SUBMITTED",
                )
            )
        }

        // Reject current version
        val now = Date()
        val version = offer.version
        if (version != null) {
            version.status = OfferStatus.REJECTED
            version.modifiedAt = now
            versionDao.save(version)
        }

        // Create new version
        val offerVersion = versionDao.save(
            OfferVersionEntity(
                tenantId = offer.tenantId,
                offer = offer,
                status = OfferStatus.SUBMITTED,
                createdById = securityService.getCurrentUserIdOrNull(),
                price = request.price,
                currency = request.currency,
                submittingParty = request.submittingParty,
                createdAt = now,
                modifiedAt = now,
                closingAt = request.closingAt,
                expiresAt = request.expiresAt,
                contingencies = request.contingencies?.ifEmpty { null },
                assigneeUserId = resolveAssigneeId(offer, request.submittingParty),
            )
        )

        // Update offer
        offer.version = offerVersion
        offer.status = offerVersion.status
        offer.totalVersions = versionDao.countByOffer(offer)?.toInt() ?: 0
        offer.modifiedAt = now
        offerDao.save(offer)
        return offerVersion
    }

    @Transactional
    fun status(id: Long, request: UpdateOfferStatusRequest, tenantId: Long): OfferEntity {
        // Check status
        val offer = get(id, tenantId)
        return status(offer, request)
    }

    private fun status(offer: OfferEntity, request: UpdateOfferStatusRequest): OfferEntity {
        // Check status
        when (request.status) {
            OfferStatus.SUBMITTED -> checkStatus(OfferStatus.UNKNOWN, offer)

            OfferStatus.ACCEPTED,
            OfferStatus.REJECTED,
            OfferStatus.WITHDRAWN,
            OfferStatus.EXPIRED -> checkStatus(OfferStatus.SUBMITTED, offer)

            OfferStatus.CLOSED,
            OfferStatus.CANCELLED -> checkStatus(OfferStatus.ACCEPTED, offer)

            else -> throw ConflictException(
                error = Error(
                    code = ErrorCode.OFFER_BAD_STATUS,
                    message = "${request.status}",
                )
            )
        }

        // Update offer
        val now = Date()
        offer.status = request.status
        offer.modifiedAt = now
        if (request.status == OfferStatus.CLOSED) {
            offer.closedAt = request.closedAt
        } else if (request.status == OfferStatus.ACCEPTED) {
            offer.acceptedAt = now
        } else if (request.status == OfferStatus.REJECTED) {
            offer.rejectedAt = now
        }
        offerDao.save(offer)

        // Update version
        val version = offer.version
        if (version != null) {
            version.status = request.status
            version.modifiedAt = now
            versionDao.save(version)
        }

        // Persist status
        statusDao.save(
            OfferStatusEntity(
                tenantId = offer.tenantId,
                offer = offer,
                version = version,
                status = request.status,
                comment = request.comment?.ifEmpty { null },
                createdAt = now,
                createdById = securityService.getCurrentUserIdOrNull(),
            )
        )

        return offer
    }

    private fun resolveAssigneeId(offer: OfferEntity, submittingParty: OfferParty): Long {
        return if (submittingParty == OfferParty.BUYER) {
            offer.sellerAgentUserId
        } else {
            offer.buyerAgentUserId
        }
    }

    private fun checkStatus(expected: OfferStatus, offer: OfferEntity) {
        if (offer.status != expected) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.OFFER_BAD_STATUS,
                    message = "Offer status is ${offer.status}. It should be $expected",
                )
            )
        }
    }
}
