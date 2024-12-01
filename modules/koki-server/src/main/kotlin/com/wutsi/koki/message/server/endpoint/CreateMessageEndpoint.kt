package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.CreateMessageResponse
import com.wutsi.koki.message.server.service.MessageService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateMessageEndpoint(private val service: MessageService) {
    @PostMapping("/v1/messages")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateMessageRequest
    ): CreateMessageResponse {
        val message = service.create(request, tenantId)
        return CreateMessageResponse(messageId = message.id!!)
    }
}
