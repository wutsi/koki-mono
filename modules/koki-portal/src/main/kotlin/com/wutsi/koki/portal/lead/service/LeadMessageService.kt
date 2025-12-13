package com.wutsi.koki.portal.lead.service

import com.wutsi.koki.portal.lead.mapper.LeadMessageMapper
import com.wutsi.koki.portal.lead.model.LeadMessageModel
import com.wutsi.koki.sdk.KokiLeadMessages
import org.springframework.stereotype.Service

@Service
class LeadMessageService(
    private val koki: KokiLeadMessages,
    private val mapper: LeadMessageMapper,
) {
    fun get(id: Long): LeadMessageModel {
        val message = koki.get(id).message
        return mapper.toLeadMessageModel(message)
    }

    fun search(
        ids: List<Long> = emptyList(),
        leadIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<LeadMessageModel> {
        val messages = koki.search(
            ids = ids,
            leadIds = leadIds,
            limit = limit,
            offset = offset,
        ).messages

        return messages.map { message -> mapper.toLeadMessageModel(message) }
    }
}
