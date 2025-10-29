package com.wutsi.koki.tenant.server.job

import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.service.InvitationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date

@Service
class InvitationCronJobs(private val service: InvitationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(InvitationCronJobs::class.java)
    }

    @Scheduled(cron = "\${koki.module.invitation.cron.expire}")
    fun expire() {
        val now = Date()
        service.searchNotExpired(now).forEach { invitation -> expire(invitation) }
    }

    private fun expire(invitation: InvitationEntity) {
        try {
            service.expire(invitation.id ?: "", invitation.tenantId)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to expire Invitation#${invitation.id}", ex)
        }
    }
}
