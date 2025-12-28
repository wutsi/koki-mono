package com.wutsi.koki.portal.offer.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.offer.mapper.OfferMapper
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.sdk.KokiOfferVersion
import com.wutsi.koki.sdk.KokiOffers
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class OfferVersionService(
    private val koki: KokiOfferVersion,
    private val kokiOffer: KokiOffers,
    private val listingService: ListingService,
    private val mapper: OfferMapper,
) {
    fun get(id: Long): OfferVersionModel {
        val version = koki.get(id).offerVersion
        val offer = kokiOffer.get(version.offerId).offer
        val listing = if (offer.owner?.id != null && offer.owner?.type == ObjectType.LISTING) {
            listingService.get(offer.owner?.id ?: -1, fullGraph = false)
        } else {
            null
        }

        return mapper.toOfferVersionModel(version, listing)
    }

    fun create(form: OfferForm): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return koki.create(
            CreateOfferVersionRequest(
                offerId = form.id,
                price = form.price ?: 0,
                currency = form.currency ?: "",
                submittingParty = form.submittingParty,
                contingencies = form.contingencies,
                expiresAt = form.expiresAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                closingAt = form.closingAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
            )
        ).versionId
    }

    fun search(
        ids: List<Long> = emptyList(),
        offerId: Long? = null,
        agentUserId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<OfferVersionModel> {
        val offerVersions = koki.search(
            ids = ids,
            offerId = offerId,
            agentUserId = agentUserId,
            limit = limit,
            offset = offset,
        ).offerVersions
        return offerVersions.map { version -> mapper.toOfferVersionModel(version, null) }
    }
}
