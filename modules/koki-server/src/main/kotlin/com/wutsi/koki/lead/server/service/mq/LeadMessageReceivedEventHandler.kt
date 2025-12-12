package com.wutsi.koki.lead.server.service.mq

import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.platform.logger.KVLogger

class LeadMessageReceivedEventHandler(
    private val leadMessageService: LeadMessageService,
    private val leadService: LeadService,
    private val logger: KVLogger,
) {
    fun handle(event: LeadMessageReceivedEvent): Boolean {
        logger.add("message_id", event.messageId)
        logger.add("tenant_id", event.tenantId)
        logger.add("new_lead", event.newLead)

        val message = leadMessageService.get(event.messageId, event.tenantId)
        message.lead.totalMessages = leadMessageService.countByLead(message.lead).toInt()
        leadService.save(message.lead)
        return true
    }
}
