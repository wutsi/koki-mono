package com.wutsi.koki.offer.server.dao

import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OfferVersionRepository : CrudRepository<OfferVersionEntity, Long> {
    fun countByOffer(offer: OfferEntity): Long?
    fun findByOfferAndStatus(offer: OfferEntity, status: OfferStatus): List<OfferVersionEntity>
}
