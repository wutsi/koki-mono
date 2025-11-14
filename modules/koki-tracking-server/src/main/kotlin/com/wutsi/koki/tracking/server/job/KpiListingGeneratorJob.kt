package com.wutsi.koki.tracking.server.job

import com.wutsi.koki.tracking.server.service.KpiListingGenerator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiListingGeneratorJob(private val generator: KpiListingGenerator) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiListingGeneratorJob::class.java)
    }

    @Scheduled(cron = "\${koki.kpi.listing.daily-cron}")
    fun daily() {
        LOGGER.info("Generating daily KPIs")
        generator.generate(LocalDate.now())
    }

    @Scheduled(cron = "\${koki.kpi.listing.monthly-cron}")
    fun monthly() {
        LOGGER.info("Generating monthly KPIs")
        generator.generate(LocalDate.now().minusMonths(1))
    }
}
