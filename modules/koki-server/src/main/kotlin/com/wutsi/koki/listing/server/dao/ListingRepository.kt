package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.ListingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingRepository : CrudRepository<ListingEntity, Long>
