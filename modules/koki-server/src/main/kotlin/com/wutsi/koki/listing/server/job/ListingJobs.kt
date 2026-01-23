package com.wutsi.koki.listing.server.job

import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/listing/jobs")
@Service
class ListingJobs(
    private val listingMetricService: ListingMetricService,
    private val tenantService: TenantService,
) {
    @Scheduled(cron = "\${koki.module.listing.cron.listing-metrics}")
    fun aggregateMetrics() {
        tenantService.all().forEach { tenant ->
            if (tenant.status == TenantStatus.ACTIVE) {
                aggregateMetrics(tenant)
            }
        }
    }

    private fun aggregateMetrics(tenant: TenantEntity) {
        val logger = DefaultKVLogger()
        try {
            logger.add("job", "ListingJobs#aggregateMetrics")
            logger.add("tenant_id", tenant.id)
            listingMetricService.aggregate(tenant.id ?: -1)
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
