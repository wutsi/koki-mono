package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.ListingLocationMetricEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingLocationMetricRepository : JpaRepository<ListingLocationMetricEntity, Long> {
    fun findByTenantId(tenantId: Long): List<ListingLocationMetricEntity>
}
