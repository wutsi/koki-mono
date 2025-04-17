package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.dto.CreateInvitationResponse
import com.wutsi.koki.account.dto.GetInvitationResponse
import com.wutsi.koki.account.dto.event.InvitationCreatedEvent
import com.wutsi.koki.account.server.mapper.InvitationMapper
import com.wutsi.koki.account.server.service.InvitationService
import com.wutsi.koki.platform.mq.Publisher
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/invitations")
class InvitationEndpoints(
    private val service: InvitationService,
    private val mapper: InvitationMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateInvitationRequest,
    ): CreateInvitationResponse {
        val invitation = service.create(request, tenantId)
        publisher.publish(
            InvitationCreatedEvent(
                invitationId = invitation.id ?: "",
                tenantId = tenantId
            )
        )
        return CreateInvitationResponse(
            invitationId = invitation.id ?: ""
        )
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetInvitationResponse {
        val invitation = service.get(id, tenantId)
        return GetInvitationResponse(
            invitation = mapper.toInvitation(invitation)
        )
    }
}
