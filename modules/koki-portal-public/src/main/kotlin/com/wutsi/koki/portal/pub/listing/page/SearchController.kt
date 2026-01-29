package com.wutsi.koki.portal.pub.listing.page

import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/search")
class SearchController(
    private val service: ListingService,
    private val locationService: LocationService,
) : AbstractPageController() {
    @GetMapping("/for-rent")
    fun forRent(
        @RequestParam(required = false) bedrooms: String? = null,
        @RequestParam(required = false) bathrooms: String? = null,
        @RequestParam(name = "location-id", required = false) locationId: Long? = null,
        model: Model,
    ): String {
        val xbedrooms = parseRooms(bedrooms)
        val xbathrooms = parseRooms(bathrooms)
        val listings = service.search(
            listingType = ListingType.RENTAL,
            minBedrooms = xbedrooms.first,
            maxBedrooms = xbedrooms.second,
            minBathrooms = xbathrooms.first,
            maxBathrooms = xbathrooms.second,
            locationIds = locationId?.let { id -> listOf(id) } ?: emptyList(),
            limit = 20,
        )
        model.addAttribute("listings", listings)

        var location = locationId?.let { id -> locationService.get(id) }
        if (location != null && location.type == LocationType.NEIGHBORHOOD) {
            val parent = location.parentId?.let { id -> locationService.get(id) }
            if (parent != null) {
                location = location.copy(name = "${location.name}, ${parent.name}")
            }
        }
        model.addAttribute("location", location)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SEARCH,
                title = location?.let {
                    getMessage("page.search.meta.title-for-rent", arrayOf(it.name))
                } ?: getMessage("listing-type.RENTAL")
            )
        )
        return "listing/search-for-rent"
    }

    private fun parseRooms(value: String?): Pair<Int?, Int?> {
        return if (value.isNullOrEmpty()) {
            Pair(null, null)
        } else if (value.endsWith("+")) {
            Pair(value.trimEnd('+').toInt(), null)
        } else {
            Pair(value.toInt(), value.toInt())
        }
    }
}
