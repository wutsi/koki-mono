package com.wutsi.koki.offer.server.dao

import com.wutsi.koki.offer.server.domain.OfferEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OfferRepository : CrudRepository<OfferEntity, Long>
