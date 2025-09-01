package com.wutsi.koki.sdk

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
import org.springframework.web.client.RestTemplate

class KokiListings(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/listings"
    }

    fun create(request: CreateListingRequest): CreateListingResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateListingResponse::class.java).body
    }

    fun update(id: Long, request: UpdateListingRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateAmenities(id: Long, request: UpdateListingAmenitiesRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/amenities")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateAddress(id: Long, request: UpdateListingAddressRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/address")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateGeoLocation(id: Long, request: UpdateListingGeoLocationRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/geo-location")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updatePrice(id: Long, request: UpdateListingPriceRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/price")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateLeasing(id: Long, request: UpdateListingLeasingRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/leasing")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updatePrice(id: Long, request: UpdateListingSellerRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/seller")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateRemarks(id: Long, request: UpdateListingRemarksRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/remarks")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun updateSeller(id: Long, request: UpdateListingSellerRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/seller")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun get(id: Long): GetListingResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetListingResponse::class.java).body
    }

    fun search(
        ids: List<Long> = emptyList(),
        listingNumber: Long? = null,
        locationIds: List<Long> = emptyList(),
        listingType: ListingType? = null,
        propertyTypes: List<PropertyType> = emptyList(),
        furnitureTypes: List<FurnitureType> = emptyList(),
        statuses: List<ListingStatus> = emptyList(),
        bedrooms: String? = null,
        bathrooms: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minLotArea: Int? = null,
        maxLotArea: Int? = null,
        minPropertyArea: Int? = null,
        maxPropertyArea: Int? = null,
        sellerAgentUserId: Long? = null,
        buyerAgentUserId: Long? = null,
        agentUserId: Long? = null,
        sortBy: ListingSort? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchListingResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "listing-number" to listingNumber,
                "location-id" to locationIds,
                "listing-type" to listingType,
                "property-type" to propertyTypes,
                "furniture-type" to furnitureTypes,
                "status" to statuses,
                "bedrooms" to bedrooms,
                "bathrooms" to bathrooms,
                "min-price" to minPrice,
                "max-price" to maxPrice,
                "min-lot-area" to minLotArea,
                "max-lot-area" to maxLotArea,
                "min-property-area" to minPropertyArea,
                "max-property-area" to maxPropertyArea,
                "seller-agent-user-id" to sellerAgentUserId,
                "buyer-agent-user-id" to buyerAgentUserId,
                "agent-user-id" to agentUserId,
                "sort-by" to sortBy,
                "limit" to limit,
                "offset" to offset,
            ),
        )
        return rest.getForEntity(url, SearchListingResponse::class.java).body
    }

    fun publish(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/publish")
        rest.postForEntity(url, emptyMap<String, Any>(), Any::class.java)
    }

    fun close(id: Long, request: CloseListingRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/close")
        rest.postForEntity(url, request, Any::class.java)
    }
}
