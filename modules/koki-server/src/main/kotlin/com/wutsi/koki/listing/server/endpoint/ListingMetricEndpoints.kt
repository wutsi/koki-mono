package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.dto.SearchListingMetricResponse
import com.wutsi.koki.listing.server.mapper.ListingMetricMapper
import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/listings/metrics")
class ListingLocationMetricEndpoints(
    private val service: ListingMetricService,
    private val mapper: ListingMetricMapper,
    private val logger: KVLogger,
) {
    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "neighbourhood-id") neighbourhoodId: Long? = null,
        @RequestParam(required = false, name = "property-category") propertyCategory: PropertyCategory? = null,
        @RequestParam(required = false, name = "listing-type") listingType: ListingType? = null,
        @RequestParam(required = false, name = "listing-status") listingStatus: ListingStatus? = null,
    ): SearchListingMetricResponse {
        val metrics = service.search(
            tenantId = tenantId,
            neighbourhoodId = neighbourhoodId,
            propertyCategory = propertyCategory,
            listingType = listingType,
            listingStatus = listingStatus
        )

        logger.add("response_metric_count", metrics.size)
        return SearchListingMetricResponse(
            metrics = metrics.map { metric -> mapper.toListingLocationMetricSummary(metric) }
        )
    }
}
