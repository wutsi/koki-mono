package com.wutsi.koki.tenant.server.job

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.service.InvitationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/v1/invitations/jobs")
@Service
class InvitationJobs(private val service: InvitationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(InvitationJobs::class.java)
    }

    @PostMapping("/expire")
    @Scheduled(cron = "\${koki.module.invitation.cron.expire}")
    fun expire() {
        val now = Date()
        var expired = 0
        val logger = DefaultKVLogger()
        try {
            logger.add("job", "InvitationJobs#expire")
            service.searchNotExpired(now).forEach { invitation ->
                expire(invitation)
                expired++
            }
        } finally {
            logger.add("expired_invitations", expired)
            logger.log()
        }
    }

    private fun expire(invitation: InvitationEntity) {
        try {
            service.expire(invitation.id ?: "", invitation.tenantId)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to expire Invitation#${invitation.id}", ex)
        }
    }
}
