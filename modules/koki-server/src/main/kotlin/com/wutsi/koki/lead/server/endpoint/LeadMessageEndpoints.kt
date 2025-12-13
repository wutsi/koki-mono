package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.lead.dto.GetLeadMessageResponse
import com.wutsi.koki.lead.dto.SearchLeadMessageResponse
import com.wutsi.koki.lead.server.mapper.LeadMessageMapper
import com.wutsi.koki.lead.server.service.LeadMessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Collections.emptyList

@RestController
@RequestMapping("/v1/lead-messages")
class LeadMessageEndpoints(
    private val service: LeadMessageService,
    private val mapper: LeadMessageMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetLeadMessageResponse {
        val message = service.get(id, tenantId)
        return GetLeadMessageResponse(
            message = mapper.toLeadMessage(message)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "lead-id") leadIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "limit") limit: Int = 20,
        @RequestParam(required = false, name = "offset") offset: Int = 0
    ): SearchLeadMessageResponse {
        val messages = service.search(
            tenantId = tenantId,
            ids = ids,
            leadIds = leadIds,
            limit = limit,
            offset = offset,
        )
        return SearchLeadMessageResponse(
            messages = messages.map { lead -> mapper.toLeadMessageSummary(lead) }
        )
    }
}
