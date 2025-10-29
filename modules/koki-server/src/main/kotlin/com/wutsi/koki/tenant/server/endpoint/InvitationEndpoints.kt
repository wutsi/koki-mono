package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.CreateInvitationResponse
import com.wutsi.koki.tenant.dto.GetInvitationResponse
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.SearchInvitationResponse
import com.wutsi.koki.tenant.dto.event.InvitationCreatedEvent
import com.wutsi.koki.tenant.server.mapper.InvitationMapper
import com.wutsi.koki.tenant.server.service.InvitationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("/v1/invitations")
class InvitationEndpoints(
    private val service: InvitationService,
    private val publisher: Publisher,
    private val mapper: InvitationMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateInvitationRequest,
    ): CreateInvitationResponse {
        val invitation = service.create(request, tenantId)
        val invitationId = invitation.id ?: ""
        publisher.publish(
            InvitationCreatedEvent(
                invitationId = invitationId,
                tenantId = tenantId
            )
        )
        return CreateInvitationResponse(
            invitationId = invitationId
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ) {
        service.delete(id, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetInvitationResponse {
        val invitation = service.get(id, tenantId)
        return GetInvitationResponse(invitation = mapper.toInvitation(invitation))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<InvitationStatus> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchInvitationResponse {
        val invitations = service.search(
            tenantId = tenantId,
            ids = ids,
            statuses = statuses,
            limit = limit,
            offset = offset
        )
        return SearchInvitationResponse(
            invitations = invitations.map { invitation -> mapper.toInvitationSummary(invitation) }
        )
    }
}
