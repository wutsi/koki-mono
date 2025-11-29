package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.GetLeadResponse
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.SearchLeadResponse
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.dto.event.LeadStatusChangedEvent
import com.wutsi.koki.lead.server.mapper.LeadMapper
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.platform.mq.Publisher
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Collections.emptyList

@RestController
@RequestMapping("/v1/leads")
class LeadEndpoints(
    private val service: LeadService,
    private val mapper: LeadMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestHeader(name = "X-Device-ID", required = false) deviceId: String? = null,
        @Valid @RequestBody request: CreateLeadRequest,
    ): CreateLeadResponse {
        val lead = service.create(request, tenantId, deviceId)
        publisher.publish(
            LeadCreatedEvent(
                leadId = lead.id ?: -1,
                tenantId = tenantId,
            )
        )
        return CreateLeadResponse(
            leadId = lead.id ?: -1
        )
    }

    @PostMapping("/{id}/status")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateLeadStatusRequest,
    ) {
        service.status(id, request, tenantId)
        publisher.publish(
            LeadStatusChangedEvent(
                leadId = id,
                status = request.status,
                tenantId = tenantId,
            )
        )
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetLeadResponse {
        val lead = service.get(id, tenantId)
        return GetLeadResponse(
            lead = mapper.toLead(lead)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "user-id") userId: Long? = null,
        @RequestParam(required = false, name = "listing-id") listingIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "agent-user-id") agentUserIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<LeadStatus> = emptyList(),
        @RequestParam(required = false, name = "limit") limit: Int = 20,
        @RequestParam(required = false, name = "offset") offset: Int = 0
    ): SearchLeadResponse {
        val leads = service.search(
            tenantId = tenantId,
            keyword = keyword,
            ids = ids,
            userId = userId,
            listingIds = listingIds,
            agentUserIds = agentUserIds,
            statuses = statuses,
            limit = limit,
            offset = offset,
        )
        return SearchLeadResponse(
            leads = leads.map { lead -> mapper.toLeadSummary(lead) }
        )
    }
}
