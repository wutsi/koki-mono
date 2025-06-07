package com.wutsi.koki.tracking.server.job

import com.wutsi.koki.tracking.server.service.filter.PersisterFilter
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PersisterJob(private val filter: PersisterFilter) {
    @Scheduled(cron = "\${koki.persister.cron}")
    fun run() {
        filter.flush()
    }
}
