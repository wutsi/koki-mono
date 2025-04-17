package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.dto.CreateInvitationResponse
import com.wutsi.koki.account.dto.GetInvitationResponse
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
class InvitationEndpoints {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateInvitationRequest,
    ): CreateInvitationResponse {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetInvitationResponse {
        TODO()
    }
}
