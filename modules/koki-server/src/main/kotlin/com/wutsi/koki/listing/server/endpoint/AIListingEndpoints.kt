package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.GetAIListingResponse
import com.wutsi.koki.listing.server.mapper.AIListingMapper
import com.wutsi.koki.listing.server.service.AIListingService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/listings")
class AIListingEndpoints(
    private val service: AIListingService,
    private val mapper: AIListingMapper,
) {
    @PostMapping("/ai")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateAIListingRequest,
    ): CreateListingResponse {
        val listing = service.create(request, tenantId)
        return CreateListingResponse(listingId = listing.id ?: -1)
    }

    @GetMapping("/{id}/ai")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAIListingResponse {
        val aiListing = service.getByListing(id, tenantId)
        return GetAIListingResponse(aiListing = mapper.toAIListing(aiListing))
    }
}
