package com.wutsi.koki.tracking.server.job

import com.wutsi.koki.tracking.server.service.KpiRoomService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiRoomJob(private val service: KpiRoomService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiRoomJob::class.java)
    }

    @Scheduled(cron = "\${koki.kpi.room.daily-cron}")
    fun daily() {
        LOGGER.info("Generating daily KPIs")
        service.generate(LocalDate.now())
    }

    @Scheduled(cron = "\${koki.kpi.room.monthly-cron}")
    fun monthly() {
        LOGGER.info("Generating monthly KPIs")
        service.generate(LocalDate.now().minusMonths(1))
    }
}
