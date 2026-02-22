package com.wutsi.koki.portal.pub.listing.page

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.service.PlaceService
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.refdata.dto.LocationType
import io.hypersistence.utils.common.LogUtils
import org.springframework.ui.Model

abstract class AbstractListingController(
    protected val listingService: ListingService,
    protected val locationService: LocationService,
    protected val placeService: PlaceService,
) : AbstractPageController() {
    protected fun loadFooterLinks(
        city: LocationModel?,
        neighbourhoodPlace: PlaceModel?,
        listingType: ListingType,
        model: Model,
    ) {
        try {
            // Cities
            loadFooterCities(city, model)

            // Neighbourhoods with active listings
            val neighbourhoodIds = listingService.metrics(
                cityId = city?.id,
                listingType = listingType,
                dimension = ListingMetricDimension.NEIGHBORHOOD,
                listingStatus = ListingStatus.ACTIVE,
            ).mapNotNull { metric -> metric.neighborhoodId }

            // Similar neighbourhoods
            val similarNeighbourhoods = if (neighbourhoodPlace?.type == PlaceType.NEIGHBORHOOD) {
                loadSimilarNeighbourhoods(neighbourhoodPlace, neighbourhoodIds, model)
            } else {
                emptyList()
            }

            // Neighbourhoods in the same city
            if (city != null) {
                loadFooterNeighbourhoods(city, similarNeighbourhoods, neighbourhoodIds, model)
            }
        } catch (ex: Exception) {
            LogUtils.LOGGER.warn("Failed to load footer cities", ex)
        }
    }

    private fun loadFooterCities(city: LocationModel?, model: Model) {
        val cityIds = placeService.search(
            types = listOf(PlaceType.CITY),
            statuses = listOf(PlaceStatus.PUBLISHED),
            limit = 13,
        ).map { place -> place.cityId }
            .filter { id -> id != city?.id }

        val cities = locationService.search(
            ids = cityIds,
            types = listOf(LocationType.CITY),
            country = tenantHolder.get().country,
            limit = cityIds.size,
        )
        if (cities.isNotEmpty()) {
            model.addAttribute("footerCities", cities.take(12))
        }
    }

    private fun loadSimilarNeighbourhoods(
        neighbourhood: PlaceModel,
        neighbourhoodIds: List<Long>,
        model: Model
    ): List<LocationModel> {
        val ids = placeService.similarNeighbourhoods(
            neighbourhood = neighbourhood,
            limit = 13,
        ).filter { similar -> similar.id != neighbourhood.id }
            .filter { similar -> neighbourhoodIds.contains(similar.neighbourhoodId) }
            .mapNotNull { similar -> similar.neighbourhoodId }
        if (ids.isEmpty()) {
            return emptyList()
        }

        val neighbourhoods = locationService.search(
            ids = ids,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = ids.size,
        )
        model.addAttribute("similarNeighbourhoods", neighbourhoods)
        return neighbourhoods
    }

    private fun loadFooterNeighbourhoods(
        city: LocationModel,
        exclude: List<LocationModel>,
        neighbourhoodIds: List<Long>,
        model: Model
    ) {
        val ids = placeService.search(
            cityIds = listOf(city.id),
            neighbourhoodIds = neighbourhoodIds,
            types = listOf(PlaceType.NEIGHBORHOOD),
            limit = 12 + exclude.size,
        ).mapNotNull { similar -> similar.neighbourhoodId }

        val excludeIds = exclude.map { it.id }
        val neighbourhoods = locationService.search(
            ids = ids,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = ids.size,
        ).filter { location -> !excludeIds.contains(location.id) }
        model.addAttribute("footerNeighbourhoods", neighbourhoods)
        model.addAttribute("footerCity", city)
    }
}
