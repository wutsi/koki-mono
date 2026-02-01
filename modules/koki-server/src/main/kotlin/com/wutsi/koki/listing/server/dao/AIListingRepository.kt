package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.AIListingEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("Persistence will be done in S3")
@Repository
interface AIListingRepository : CrudRepository<AIListingEntity, Long> {
    fun findByListing(listing: ListingEntity): AIListingEntity?
}
