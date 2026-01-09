package com.wutsi.koki.listing.server.job

import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ListingMetricJobs(
    private val listingMetricService: ListingMetricService,
    private val tenantService: TenantService,
) {
    @Scheduled(cron = "\${koki.module.listing.metrics.jobs.daily}")
    fun daily() {
        tenantService.all().forEach { tenant ->
            aggregate(tenant)
        }
    }

    private fun aggregate(tenant: TenantEntity) {
        val logger = DefaultKVLogger()
        try {
            logger.add("job", "ListingMetricJobs#daily")
            logger.add("tenant_id", tenant.id)
            listingMetricService.aggregate(tenant.id ?: -1)
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
