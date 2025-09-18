package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionResponse
import com.wutsi.koki.offer.dto.GetOfferVersionResponse
import com.wutsi.koki.offer.dto.SearchOfferVersionResponse
import com.wutsi.koki.offer.dto.event.OfferVersionCreatedEvent
import com.wutsi.koki.offer.server.mapper.OfferVersionMapper
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.offer.server.service.OfferVersionService
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
@RequestMapping("/v1/offer-versions")
class OfferVersionEndpoints(
    private val offerService: OfferService,
    private val versionService: OfferVersionService,
    private val mapper: OfferVersionMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateOfferVersionRequest,
    ): CreateOfferVersionResponse {
        val version = offerService.create(request, tenantId)
        val offer = version.offer
        publisher.publish(
            OfferVersionCreatedEvent(
                offerId = request.offerId,
                versionId = version.id ?: -1,
                tenantId = tenantId,
                owner = if (offer.ownerId != null && offer.ownerType != null) {
                    ObjectReference(offer.ownerId, offer.ownerType)
                } else {
                    null
                },
            )
        )
        return CreateOfferVersionResponse(version.id ?: -1)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetOfferVersionResponse {
        val version = versionService.get(id, tenantId)
        return GetOfferVersionResponse(
            offerVersion = mapper.toOfferVersion(version)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "offer-id") offerId: Long? = null,
        @RequestParam(required = false, name = "agent-user-id") agentUserId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchOfferVersionResponse {
        val versions = versionService.search(
            tenantId = tenantId,
            ids = ids,
            offerId = offerId,
            agentUserId = agentUserId,
            limit = limit,
            offset = offset,
        )
        return SearchOfferVersionResponse(
            offerVersions = versions.map { version -> mapper.toOfferVersionSummary(version) }
        )
    }
}
