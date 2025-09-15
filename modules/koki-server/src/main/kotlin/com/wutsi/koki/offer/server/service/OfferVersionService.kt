package com.wutsi.koki.offer.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.server.dao.OfferRepository
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class OfferService(
    private val offerDao: OfferRepository,
    private val versionDao: OfferVersionRepository,
    private val securityService: SecurityService,
) {
    fun get(id: Long, tenantId: Long): OfferEntity {
        val user = offerDao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.OFFER_NOT_FOUND)) }

        if (user.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.OFFER_NOT_FOUND))
        }
        return user
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
                status = OfferStatus.SUBMITTED,
                ownerType = request.owner?.type,
                ownerId = request.owner?.id,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
                totalVersions = 1,
            )
        )

        // Create version
        val version = versionDao.save(
            OfferVersionEntity(
                tenantId = tenantId,
                offer = offer,
                status = offer.status,
                createdById = userId,
                price = request.price,
                currency = request.currency,
                submittingParty = request.submittingParty,
                createdAt = now,
                closingAt = request.closingAt,
                expiresAt = request.expiresAt,
                contingencies = request.contingencies,
            )
        )

        // Set version
        offer.version = version
        offerDao.save(offer)
        return offer
    }

    @Transactional
    fun create(id: Long, request: CreateOfferVersionRequest, tenantId: Long): OfferVersionEntity {
        val offer = get(id, tenantId)

        // Create version
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val version = versionDao.save(
            OfferVersionEntity(
                tenantId = tenantId,
                offer = offer,
                status = offer.status,
                createdById = userId,
                price = request.price,
                currency = request.currency,
                submittingParty = request.submittingParty,
                createdAt = now,
                closingAt = request.closingAt,
                expiresAt = request.expiresAt,
                contingencies = request.contingencies,
            )
        )

        // Set version
        offer.version = version
        offer.status = version.status
        offer.totalVersions = versionDao.countByOffer(offer)?.toInt() ?: 0
        offer.modifiedAt = now
        offerDao.save(offer)
        return version
    }
}
