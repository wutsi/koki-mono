package com.wutsi.koki.room.server.job

import com.wutsi.koki.room.server.service.RoomLocationMetricService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RoomLocationMetricJob(
    private val service: RoomLocationMetricService,
) {
    @Scheduled(cron = "\${koki.module.room.metric.location.cron}")
    fun run() {
        service.compile()
    }
}
