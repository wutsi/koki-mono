package com.wutsi.koki.portal.offer.mapper

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.Offer
import com.wutsi.koki.offer.dto.OfferSummary
import com.wutsi.koki.offer.dto.OfferVersion
import com.wutsi.koki.offer.dto.OfferVersionSummary
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.refdata.dto.Money
import org.springframework.stereotype.Service

@Service
class OfferMapper(
    private val moneyMapper: MoneyMapper
) : TenantAwareMapper() {
    fun toOfferModel(
        entity: Offer,
        listing: ListingModel?,
        contacts: Map<Long, ContactModel>,
        users: Map<Long, UserModel>,
    ): OfferModel {
        return OfferModel(
            id = entity.id,
            version = toOfferVersionModel(entity.version, listing),
            totalVersions = entity.totalVersions,
            owner = entity.owner?.let { owner -> ObjectReferenceModel(owner.id, owner.type) },
            listing = listing,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            sellerAgentUser = users[entity.sellerAgentUserId] ?: UserModel(id = entity.sellerAgentUserId),
            buyerAgentUser = users[entity.buyerAgentUserId] ?: UserModel(id = entity.buyerAgentUserId),
            buyerContact = contacts[entity.buyerContactId] ?: ContactModel(id = entity.buyerContactId),
            status = entity.status,
            readOnly = if (listing != null) {
                (listing.statusOnMarket == false)
            } else {
                false
            },
        )
    }

    fun toOfferModel(
        entity: OfferSummary,
        versions: Map<Long, OfferVersionSummary>,
        listings: Map<Long, ListingModel>,
        contacts: Map<Long, ContactModel>,
        users: Map<Long, UserModel>,
    ): OfferModel {
        val listing = when (entity.owner?.type) {
            ObjectType.LISTING -> entity.owner?.id?.let { id -> listings[id] }
            else -> null
        }

        return OfferModel(
            id = entity.id,
            version = versions[entity.versionId]?.let { version ->
                toOfferVersionModel(version, listing)
            } ?: OfferVersionModel(),
            totalVersions = entity.totalVersions,
            owner = entity.owner?.let { owner -> ObjectReferenceModel(owner.id, owner.type) },
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            sellerAgentUser = users[entity.sellerAgentUserId] ?: UserModel(id = entity.sellerAgentUserId),
            buyerAgentUser = users[entity.buyerAgentUserId] ?: UserModel(id = entity.buyerAgentUserId),
            buyerContact = contacts[entity.buyerContactId] ?: ContactModel(id = entity.buyerContactId),
            status = entity.status,

            listing = listing,
        )
    }

    fun toOfferVersionModel(
        entity: OfferVersion,
        listing: ListingModel?,
    ): OfferVersionModel {
        val df = createMediumDateFormat()
        val priceDiff = listing?.price?.let { price ->
            Money(
                amount = entity.price.amount - price.amount,
                currency = price.currency,
            )
        }
        return OfferVersionModel(
            id = entity.id,
            offerId = entity.offerId,
            price = moneyMapper.toMoneyModel(entity.price),
            priceDiff = priceDiff?.let { price -> moneyMapper.toMoneyModel(price) },
            status = entity.status,
            submittingParty = entity.submittingParty,
            assigneeUserId = entity.assigneeUserId,
            contingencies = entity.contingencies,
            createdAt = entity.createdAt,
            expiresAt = entity.expiresAt,
            closingAt = entity.closingAt,
            createdAtText = df.format(entity.createdAt),
            expiresAtText = entity.expiresAt?.let { date -> df.format(date) },
            closingAtText = entity.closingAt?.let { date -> df.format(date) },
        )
    }

    fun toOfferVersionModel(
        entity: OfferVersionSummary,
        listing: ListingModel?,
    ): OfferVersionModel {
        val df = createMediumDateFormat()
        val priceDiff = listing?.price?.let { price ->
            Money(
                amount = entity.price.amount - price.amount,
                currency = price.currency,
            )
        }
        return OfferVersionModel(
            id = entity.id,
            offerId = entity.offerId,
            price = moneyMapper.toMoneyModel(entity.price),
            priceDiff = priceDiff?.let { price -> moneyMapper.toMoneyModel(price) },
            status = entity.status,
            submittingParty = entity.submittingParty,
            assigneeUserId = entity.assigneeUserId,
            createdAt = entity.createdAt,
            expiresAt = entity.expiresAt,
            closingAt = entity.closingAt,
            createdAtText = df.format(entity.createdAt),
            expiresAtText = entity.expiresAt?.let { date -> df.format(date) },
            closingAtText = entity.closingAt?.let { date -> df.format(date) },
        )
    }
}
