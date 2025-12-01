package com.wutsi.koki.agent.server.mq

import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import com.wutsi.koki.tenant.server.service.InvitationService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.stereotype.Service

@Service
class AgentUserCreatedEventHandler(
    private val userService: UserService,
    private val invitationService: InvitationService,
    private val agentService: AgentService,
    private val logger: KVLogger
) {
    fun handle(event: UserCreatedEvent) {
        logger.add("event_user_id", event.userId)
        logger.add("event_invitation_id", event.invitationId)

        val user = userService.get(event.userId, event.tenantId)
        if (user.invitationId == null) {
            return
        }

        val invitation = invitationService.get(user.invitationId, user.tenantId)
        logger.add("event_invitation_type", invitation.type)
        if (invitation.type == InvitationType.AGENT) {
            val agent = agentService.create(event.userId, event.tenantId)
            logger.add("agent_id", agent.id)
        }
    }
}
