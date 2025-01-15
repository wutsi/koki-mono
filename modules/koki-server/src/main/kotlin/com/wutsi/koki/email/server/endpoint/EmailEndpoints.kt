package com.wutsi.koki.email.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.GetEmailResponse
import com.wutsi.koki.email.dto.SearchEmailResponse
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import com.wutsi.koki.email.server.mapper.EmailMapper
import com.wutsi.koki.email.server.service.EmailService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/emails")
class EmailEndpoints(
    private val service: EmailService,
    private val mapper: EmailMapper,
) {
    @PostMapping
    fun send(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: SendEmailRequest,
    ): SendEmailResponse {
        val email = service.send(request, tenantId)
        return SendEmailResponse(
            emailId = email.id!!
        )
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetEmailResponse {
        val email = service.get(id, tenantId)
        return GetEmailResponse(
            email = mapper.toEmail(email)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchEmailResponse {
        val emails = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset,
        )
        return SearchEmailResponse(
            emails = emails.map { email -> mapper.toEmailSummary(email) }
        )
    }
}
