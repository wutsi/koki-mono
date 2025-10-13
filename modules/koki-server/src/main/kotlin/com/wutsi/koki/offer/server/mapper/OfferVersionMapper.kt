package com.wutsi.koki.offer.server.mapper

import com.wutsi.koki.offer.dto.OfferVersion
import com.wutsi.koki.offer.dto.OfferVersionSummary
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import com.wutsi.koki.refdata.dto.Money
import org.springframework.stereotype.Service

@Service
class OfferVersionMapper {
    fun toOfferVersion(entity: OfferVersionEntity): OfferVersion {
        return OfferVersion(
            id = entity.id ?: -1,
            offerId = entity.offer.id ?: -1,
            price = Money(entity.price.toDouble(), entity.currency),
            status = entity.status,
            expiresAt = entity.expiresAt,
            submittingParty = entity.submittingParty,
            assigneeUserId = entity.assigneeUserId,
            contingencies = entity.contingencies,
            createdAt = entity.createdAt,
            closingAt = entity.closingAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toOfferVersionSummary(entity: OfferVersionEntity): OfferVersionSummary {
        return OfferVersionSummary(
            id = entity.id ?: -1,
            offerId = entity.offer.id ?: -1,
            price = Money(entity.price.toDouble(), entity.currency),
            status = entity.status,
            expiresAt = entity.expiresAt,
            submittingParty = entity.submittingParty,
            assigneeUserId = entity.assigneeUserId,
            createdAt = entity.createdAt,
            closingAt = entity.closingAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
