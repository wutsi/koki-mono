package com.wutsi.koki.offer.server.dao

import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.domain.OfferStatusEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OfferStatusRepository : CrudRepository<OfferStatusEntity, Long> {
    fun findByOfferOrderByIdDesc(offer: OfferEntity): List<OfferStatusEntity>
}
