package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.listing.dto.ChangeListingStatusRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.GetListingResponse
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
import com.wutsi.koki.listing.server.mapper.ListingMapper
import com.wutsi.koki.listing.server.service.ListingService
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
@RequestMapping("/v1/listings")
class ListingEndpoints(
    private val service: ListingService,
    private val mapper: ListingMapper,
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
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "neighbourhood-id") neighborhoodId: Long? = null,
        @RequestParam(required = false, name = "listing-type") listingType: ListingType? = null,
        @RequestParam(required = false, name = "property-type") propertyTypes: List<PropertyType> = emptyList(),
        @RequestParam(required = false, name = "furniture-type") furnitureTypes: List<FurnitureType> = emptyList(),
        @RequestParam(required = false, name = "min-bedrooms") minBedrooms: Int? = null,
        @RequestParam(required = false, name = "max-bedrooms") maxBedrooms: Int? = null,
        @RequestParam(required = false, name = "min-bathrooms") minBathrooms: Int? = null,
        @RequestParam(required = false, name = "max-bathrooms") maxBathrooms: Int? = null,
        @RequestParam(required = false, name = "min-price") minPrice: Double? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "min-lot-area") minLotArea: Int? = null,
        @RequestParam(required = false, name = "max-lot-area") maxLotArea: Int? = null,
        @RequestParam(required = false, name = "min-property-area") minPropertyArea: Int? = null,
        @RequestParam(required = false, name = "max-property-area") maxPropertyArea: Int? = null,
    ): SearchListingResponse {
        TODO()
    }

    @PostMapping("/{id}/status")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody request: ChangeListingStatusRequest
    ) {
        TODO()
    }
}
