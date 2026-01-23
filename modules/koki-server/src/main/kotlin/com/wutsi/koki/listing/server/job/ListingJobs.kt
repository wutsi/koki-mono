package com.wutsi.koki.listing.server.job

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import io.swagger.v3.oas.annotations.Operation
import org.apache.commons.lang3.time.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/v1/listings/jobs")
@Service
class ListingJobs(
    private val listingService: ListingService,
    private val listingMetricService: ListingMetricService,
    private val tenantService: TenantService,
    private val publisher: Publisher,
) {
    @PostMapping("/aggregate-metrics")
    @Operation(summary = "Run the job to aggregate listing metrics for all tenants")
    @Scheduled(cron = "\${koki.module.listing.cron.aggregate-metrics}")
    fun aggregateMetrics() {
        tenantService.all().forEach { tenant ->
            if (tenant.status == TenantStatus.ACTIVE) {
                aggregateMetrics(tenant)
            }
        }
    }

    @PostMapping("/publishing")
    @Operation(summary = "Run the job to publish stalled listings")
    @Scheduled(cron = "\${koki.module.listing.cron.publishing}")
    fun publishing() {
        tenantService.all().forEach { tenant ->
            if (tenant.status == TenantStatus.ACTIVE) {
                publishing(tenant)
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

    private fun publishing(tenant: TenantEntity) {
        val logger = DefaultKVLogger()
        val threshold = DateUtils.addHours(Date(), -6)
        var publishCount = 0
        try {
            logger.add("job", "ListingJobs#publishing")
            logger.add("tenant_id", tenant.id)

            listingService.search(
                tenantId = tenant.id ?: -1,
                statuses = listOf(ListingStatus.PUBLISHING),
                limit = Integer.MAX_VALUE,
            ).forEach { listing ->
                if (listing.modifiedAt.before(threshold)) {
                    // Publish only listings that have not been updated for more than 6 hours
                    publisher.publish(
                        ListingStatusChangedEvent(
                            listingId = listing.id ?: -1,
                            tenantId = listing.tenantId,
                            status = ListingStatus.PUBLISHING,
                        )
                    )
                    publishCount++
                }
            }
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.add("listing_publish_count", publishCount)
            logger.log()
        }
    }
}
