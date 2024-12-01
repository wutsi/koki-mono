package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.server.mapper.MessageMapper
import com.wutsi.koki.message.server.service.MessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetMessageEndpoint(
    private val service: MessageService,
    private val mapper: MessageMapper
) {
    @GetMapping("/v1/messages/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetMessageResponse {
        val message = service.get(id, tenantId)
        return GetMessageResponse(
            message = mapper.toMessage(message)
        )
    }
}
