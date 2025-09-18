package com.wutsi.koki.portal.offer.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.offer.mapper.OfferMapper
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiOffer
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class OfferService(
    private val koki: KokiOffer,
    private val mapper: OfferMapper,
    private val listingService: ListingService,
    private val userService: UserService,
    private val contactService: ContactService,
) {
    fun get(id: Long): OfferModel {
        val offer = koki.get(id).offer

        val listing = if (offer.owner?.type == ObjectType.LISTING) {
            offer.owner?.id?.let { ownerId -> listingService.get(ownerId) }
        } else {
            null
        }

        val userIds = listOf(offer.buyerAgentUserId, offer.sellerAgentUserId).distinct()
        val users = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                ids = userIds,
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        val contact = contactService.get(offer.buyerContactId)

        return mapper.toOfferModel(
            entity = offer,
            users = users,
            listing = listing,
            contacts = mapOf(contact.id to contact)
        )
    }

    fun search(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        agentUserId: Long? = null,
        statuses: List<OfferStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<OfferModel> {
        val offers = koki.search(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            agentUserId = agentUserId,
            statuses = statuses,
            limit = limit,
            offset = offset,
        ).offers

        val listingIds = offers
            .filter { offer -> offer.owner?.type == ObjectType.LISTING }
            .map { offer -> offer.owner?.id }
            .filterNotNull()
        val listings = if (listingIds.isEmpty()) {
            emptyMap()
        } else {
            listingService.search(
                ids = listingIds,
                limit = listingIds.size,
            ).items.associateBy { listing -> listing.id }
        }

        val userIds = offers.flatMap { offer -> listOf(offer.buyerAgentUserId, offer.sellerAgentUserId) }.distinct()
        val users = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                ids = userIds,
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        val contactIds = offers.map { offer -> offer.buyerContactId }.distinct()
        val contacts = if (contactIds.isEmpty()) {
            emptyMap()
        } else {
            contactService.search(
                ids = contactIds,
                limit = contactIds.size,
            ).associateBy { contact -> contact.id }
        }

        val versionIds = offers.map{offer -> offer.versionId}.distinct()
        val version = if (versionIds.isEmpty()){
            emptyMap()
        } else {

        }
    }

    fun create(form: OfferForm): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return koki.create(
            CreateOfferRequest(
                owner = if (form.ownerId != null && form.ownerType != null) {
                    ObjectReference(form.ownerId, form.ownerType)
                } else {
                    null
                },
                buyerAgentUserId = form.buyerAgentUserId,
                buyerContactId = form.buyerContactId,
                sellerAgentUserId = form.sellerAgentUserId,
                price = form.price ?: 0,
                currency = form.currency ?: "",
                submittingParty = OfferParty.BUYER,
                contingencies = form.contingencies,
                expiresAt = form.expiresAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                closingAt = form.closingAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
            )
        ).offerId
    }
}
