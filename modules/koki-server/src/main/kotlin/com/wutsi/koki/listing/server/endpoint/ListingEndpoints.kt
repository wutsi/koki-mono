package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.dto.UpdateListingRemarksRequest
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.mapper.ListingMapper
import com.wutsi.koki.listing.server.service.ListingService
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
import java.net.URLDecoder

@RestController
@RequestMapping("/v1/listings")
class ListingEndpoints(
    private val service: ListingService,
    private val mapper: ListingMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateListingRequest,
    ): CreateListingResponse {
        val listing = service.create(request, tenantId)
        return CreateListingResponse(
            listingId = listing.id ?: -1
        )
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @PostMapping("/{id}/amenities")
    fun updateAmenities(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingAmenitiesRequest,
    ) {
        service.amenities(id, request, tenantId)
    }

    @PostMapping("/{id}/address")
    fun updateAddress(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingAddressRequest,
    ) {
        service.address(id, request, tenantId)
    }

    @PostMapping("/{id}/geo-location")
    fun updateGeoLocation(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingGeoLocationRequest,
    ) {
        service.geoLocation(id, request, tenantId)
    }

    @PostMapping("/{id}/price")
    fun updatePrice(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingPriceRequest,
    ) {
        service.price(id, request, tenantId)
    }

    @PostMapping("/{id}/leasing")
    fun updateLeasing(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingLeasingRequest,
    ) {
        service.leasing(id, request, tenantId)
    }

    @PostMapping("/{id}/seller")
    fun updatePrice(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingSellerRequest,
    ) {
        service.seller(id, request, tenantId)
    }

    @PostMapping("/{id}/remarks")
    fun updateRemarks(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateListingRemarksRequest,
    ) {
        service.remarks(id, request, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetListingResponse {
        val listing = service.get(id, tenantId)
        return GetListingResponse(
            listing = mapper.toListing(listing)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "listing-number") listingNumber: Long? = null,
        @RequestParam(required = false, name = "location-id") locationIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "listing-type") listingType: ListingType? = null,
        @RequestParam(required = false, name = "property-type") propertyTypes: List<PropertyType> = emptyList(),
        @RequestParam(required = false, name = "furniture-type") furnitureTypes: List<FurnitureType> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<ListingStatus> = emptyList(),
        @RequestParam(required = false, name = "bedrooms") bedrooms: String? = null,
        @RequestParam(required = false, name = "bathrooms") bathrooms: String? = null,
        @RequestParam(required = false, name = "min-price") minPrice: Double? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "min-lot-area") minLotArea: Int? = null,
        @RequestParam(required = false, name = "max-lot-area") maxLotArea: Int? = null,
        @RequestParam(required = false, name = "min-property-area") minPropertyArea: Int? = null,
        @RequestParam(required = false, name = "max-property-area") maxPropertyArea: Int? = null,
        @RequestParam(required = false, name = "seller-agent-user-id") sellerAgentUserId: Long? = null,
        @RequestParam(required = false, name = "buyer-agent-user-id") buyerAgentUserId: Long? = null,
        @RequestParam(required = false, name = "agent-user-id") agentUserId: Long? = null,
        @RequestParam(required = false, name = "sort-by") sortBy: ListingSort? = null,
        @RequestParam(required = false, name = "limit") limit: Int = 20,
        @RequestParam(required = false, name = "offset") offset: Int = 0
    ): SearchListingResponse {
        val listings = service.search(
            tenantId = tenantId,
            ids = ids,
            listingNumber = listingNumber,
            locationIds = locationIds,
            listingType = listingType,
            propertyTypes = propertyTypes,
            furnitureTypes = furnitureTypes,
            statuses = statuses,
            bedrooms = bedrooms?.let { URLDecoder.decode(bedrooms, "utf-8") },
            bathrooms = bathrooms?.let { URLDecoder.decode(bathrooms, "utf-8") },
            minPrice = minPrice,
            maxPrice = maxPrice,
            minLotArea = minLotArea,
            maxLotArea = maxLotArea,
            minPropertyArea = minPropertyArea,
            maxPropertyArea = maxPropertyArea,
            sellerAgentUserId = sellerAgentUserId,
            buyerAgentUserId = buyerAgentUserId,
            agentUserId = agentUserId,
            sortBy = sortBy,
            limit = limit,
            offset = offset,
        )
        val total = service.count(
            tenantId = tenantId,
            ids = ids,
            listingNumber = listingNumber,
            locationIds = locationIds,
            listingType = listingType,
            propertyTypes = propertyTypes,
            furnitureTypes = furnitureTypes,
            statuses = statuses,
            bedrooms = bedrooms?.let { URLDecoder.decode(bedrooms, "utf-8") },
            bathrooms = bathrooms?.let { URLDecoder.decode(bathrooms, "utf-8") },
            minPrice = minPrice,
            maxPrice = maxPrice,
            minLotArea = minLotArea,
            maxLotArea = maxLotArea,
            minPropertyArea = minPropertyArea,
            maxPropertyArea = maxPropertyArea,
            sellerAgentUserId = sellerAgentUserId,
            buyerAgentUserId = buyerAgentUserId,
            agentUserId = agentUserId,
        )
        return SearchListingResponse(
            total = total,
            listings = listings.map { listing -> mapper.toListingSummary(listing) }
        )
    }

    @PostMapping("/{id}/publish")
    fun publish(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        val listing = service.publish(id, tenantId)
        publisher.publish(
            ListingStatusChangedEvent(
                listingId = id,
                tenantId = tenantId,
                status = listing.status
            )
        )
    }

    @PostMapping("/{id}/close")
    fun close(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: CloseListingRequest,
        @PathVariable id: Long,
    ) {
        val listing = service.close(id, request, tenantId)
        publisher.publish(
            ListingStatusChangedEvent(
                listingId = id,
                tenantId = tenantId,
                status = listing.status
            )
        )
    }
}
