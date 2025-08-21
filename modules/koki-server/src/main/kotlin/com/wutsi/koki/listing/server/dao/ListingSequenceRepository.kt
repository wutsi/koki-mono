package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.ListingSequenceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingSequenceRepository : CrudRepository<ListingSequenceEntity, Long> {
    fun findByTenantId(tenantId: Long): ListingSequenceEntity?
}
