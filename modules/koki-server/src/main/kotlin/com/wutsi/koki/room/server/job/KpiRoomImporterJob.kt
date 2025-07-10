package com.wutsi.koki.room.server.job

import com.wutsi.koki.room.server.service.KpiRoomImporter
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiRoomImporterJob(private val importer: KpiRoomImporter) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiRoomImporterJob::class.java)
    }

    @Scheduled(cron = "\${koki.kpi.daily-cron}")
    fun daily() {
        LOGGER.info("Importing daily KPIs")
        importer.import(LocalDate.now())
    }

    @Scheduled(cron = "\${koki.kpi.monthly-cron}")
    fun monthly() {
        LOGGER.info("Importing monthly KPIs")
        importer.import(LocalDate.now().minusMonths(1))
    }
}
