package com.wutsi.koki.offer.server.mapper

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.offer.dto.Offer
import com.wutsi.koki.offer.dto.OfferSummary
import com.wutsi.koki.offer.dto.OfferVersion
import com.wutsi.koki.offer.server.domain.OfferEntity
import org.springframework.stereotype.Service

@Service
class OfferMapper(private val versionMapper: OfferVersionMapper) {
    fun toOffer(entity: OfferEntity): Offer {
        return Offer(
            id = entity.id ?: -1,
            version = entity.version?.let { version -> versionMapper.toOfferVersion(version) } ?: OfferVersion(),
            status = entity.status,
            totalVersions = entity.totalVersions,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            buyerContactId = entity.buyerContactId,
            buyerAgentUserId = entity.buyerAgentUserId,
            sellerAgentUserId = entity.sellerAgentUserId,
            owner = if (entity.ownerId != null && entity.ownerType != null) {
                ObjectReference(entity.ownerId, entity.ownerType)
            } else {
                null
            },
        )
    }

    fun toOfferSummary(entity: OfferEntity): OfferSummary {
        return OfferSummary(
            id = entity.id ?: -1,
            versionId = entity.version?.id ?: -1,
            status = entity.status,
            totalVersions = entity.totalVersions,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            buyerContactId = entity.buyerContactId,
            buyerAgentUserId = entity.buyerAgentUserId,
            sellerAgentUserId = entity.sellerAgentUserId,
            owner = if (entity.ownerId != null && entity.ownerType != null) {
                ObjectReference(entity.ownerId, entity.ownerType)
            } else {
                null
            },
        )
    }
}
