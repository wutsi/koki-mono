package com.wutsi.koki.agent.server.job

import com.wutsi.koki.agent.server.service.AgentMetricService
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Service
class AgentMetricJobs(
    private val agentService: AgentService,
    private val metricService: AgentMetricService,
    private val logger: KVLogger
) {
    @Scheduled(cron = "\${koki.module.agent.metrics.jobs.daily}")
    fun daily() {
        logger.add("job", "AgentMetricJobs#daily")

        val date = LocalDate.now().minusDays(1)
        logger.add("date", date)

        run(date)
    }

    @Scheduled(cron = "\${koki.module.agent.metrics.jobs.monthly}")
    fun monthly() {
        logger.add("job", "AgentMetricJobs#monthly")

        val lastMonth = LocalDate.now().minusMonths(1)
        val date = LocalDate.of(lastMonth.year, lastMonth.monthValue, 1)
        logger.add("date", date)

        run(date)
    }

    private fun run(date: LocalDate) {
        val since = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val agents = agentService.getByLastSoldAtIsAfter(since)
        logger.add("agent_count", agents.size)

        agents.forEach { agent -> metricService.updateMetrics(agent) }
    }
}
