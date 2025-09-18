package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.SearchOfferResponse
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.offer.server.mapper.OfferMapper
import com.wutsi.koki.offer.server.service.OfferService
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
@RequestMapping("/v1/offers")
class OfferEndpoints(
    private val service: OfferService,
    private val mapper: OfferMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateOfferRequest,
    ): CreateOfferResponse {
        val offer = service.create(request, tenantId)
        publisher.publish(
            OfferSubmittedEvent(
                offerId = offer.id ?: -1,
                tenantId = tenantId,
                owner = if (offer.ownerId != null && offer.ownerType != null) {
                    ObjectReference(offer.ownerId, offer.ownerType)
                } else {
                    null
                },
            )
        )
        return CreateOfferResponse(offer.id ?: -1)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetOfferResponse {
        val offer = service.get(id, tenantId)
        return GetOfferResponse(mapper.toOffer(offer))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "agent-user-id") agentUserId: Long? = null,
        @RequestParam(required = false, name = "assignee-user-id") assigneeUserId: Long? = null,
        @RequestParam(required = false, name = "status") statuses: List<OfferStatus> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchOfferResponse {
        val offers = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            agentUserId = agentUserId,
            assigneeUserId = assigneeUserId,
            statuses = statuses,
            limit = limit,
            offset = offset
        )
        return SearchOfferResponse(
            offers = offers.map { offer -> mapper.toOfferSummary(offer) }
        )
    }

    @PostMapping("/{id}/status")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateOfferStatusRequest,
    ) {
        val offer = service.status(id, request, tenantId)
        publisher.publish(
            OfferStatusChangedEvent(
                offerId = id,
                tenantId = tenantId,
                status = request.status,
                owner = if (offer.ownerId != null && offer.ownerType != null) {
                    ObjectReference(offer.ownerId, offer.ownerType)
                } else {
                    null
                },
            )
        )
    }
}
