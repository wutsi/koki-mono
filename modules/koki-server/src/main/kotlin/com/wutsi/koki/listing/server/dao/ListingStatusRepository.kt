package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.domain.ListingStatusEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingStatusRepository : CrudRepository<ListingStatusEntity, Long> {
    fun findByListing(listing: ListingEntity): List<ListingStatusEntity>
}
