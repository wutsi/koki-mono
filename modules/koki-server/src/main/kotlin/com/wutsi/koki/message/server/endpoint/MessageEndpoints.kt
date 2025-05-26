package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.mapper.MessageMapper
import com.wutsi.koki.message.server.service.MessageService
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

@RestController
@RequestMapping("/v1/messages")
class MessageEndpoints(
    private val service: MessageService,
    private val mapper: MessageMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun send(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: SendMessageRequest,
    ): SendMessageResponse {
        val message = service.send(request, tenantId)
        publisher.publish(
            MessageSentEvent(
                messageId = message.id ?: -1,
                tenantId = tenantId,
                owner = request.owner,
            )
        )
        return SendMessageResponse(message.id ?: -1)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetMessageResponse {
        val message = service.get(id, tenantId)
        return GetMessageResponse(
            message = mapper.toMessage(message)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "status") statuses: List<MessageStatus> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchMessageResponse {
        val messages = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            statuses = statuses,
            limit = limit,
            offset = offset,
        )
        return SearchMessageResponse(
            messages = messages.map { message -> mapper.toMessageSummary(message) }
        )
    }

    @PostMapping("/{id}/status")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMessageStatusRequest,
    ) {
        service.status(id, request, tenantId)
    }
}
