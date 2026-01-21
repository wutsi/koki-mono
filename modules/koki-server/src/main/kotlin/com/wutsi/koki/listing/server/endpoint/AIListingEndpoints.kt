package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.server.service.AIListingService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/listings")
class AIListingEndpoints(
    private val service: AIListingService
) {
    @PostMapping("/ai")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateAIListingRequest,
    ): CreateListingResponse {
        val listing = service.create(request, tenantId)
        return CreateListingResponse(listingId = listing.id ?: -1)
    }
}
