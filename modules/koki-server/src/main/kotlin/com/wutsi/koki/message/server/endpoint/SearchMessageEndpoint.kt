package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.message.dto.UpdateMessageRequest
import com.wutsi.koki.message.server.service.MessageService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateMessageEndpoint(private val service: MessageService) {
    @PostMapping("/v1/messages/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateMessageRequest
    ) {
        service.update(id, request, tenantId)
    }
}
