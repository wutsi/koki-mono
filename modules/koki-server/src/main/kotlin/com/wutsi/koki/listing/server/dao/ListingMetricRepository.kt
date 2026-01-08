package com.wutsi.koki.listing.server.dao

import com.wutsi.koki.listing.server.domain.ListingMetricEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingMetricRepository : JpaRepository<ListingMetricEntity, Long> {
    fun findByTenantId(tenantId: Long): List<ListingMetricEntity>
}
