package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingFilterForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/listings/search")
@RequiresPermission(["listing", "listing:full_access"])
class SearchListingController : AbstractListingController() {
    @GetMapping
    fun search(@ModelAttribute form: ListingFilterForm, model: Model): String {
        loadListings(form, model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_SEARCH,
                title = getMessage("page.listing.list.meta.title"),
            )
        )

        val urlBuilder = URLBuilder("")
        val params = mapOf(
            "listingNumber" to form.listingNumber,
            "propertyType" to form.propertyTypes,
            "listingType" to form.listingType,
            "locationId" to form.locationIds,
            "bedrooms" to form.bedrooms,
            "bathrooms" to form.bathrooms,
            "minPrice" to form.minPrice,
            "maxPrice" to form.maxPrice,
            "minLotArea" to form.minLotArea,
            "maxLotArea" to form.maxLotArea,
            "minPropertyArea" to form.minPropertyArea,
            "maxPropertyArea" to form.maxPropertyArea,
            "sellerAgentUserId" to form.sellerAgentUserId,
        )
        model.addAttribute("filterUrl", urlBuilder.build("/listings/search/filter", params))

        return "listings/search"
    }

    @GetMapping("/filter")
    fun filter(@ModelAttribute form: ListingFilterForm, model: Model): String {
        model.addAttribute("form", form)
        if (form.locationIds.isNotEmpty()) {
            val locations = locationService.search(
                ids = form.locationIds,
                limit = form.locationIds.size
            )
            if (locations.isNotEmpty()) {
                model.addAttribute("locations", locations)
            }
        }

        model.addAttribute("listingTypes", ListingType.entries)
        model.addAttribute("propertyTypes", PropertyType.entries)
        model.addAttribute("rooms", listOf("1", "1+", "2", "2+", "3", "3+"))

        return "listings/filter"
    }

    private fun loadListings(form: ListingFilterForm, model: Model) {
        val listings = listingService.search(
            sortBy = when (form.listingType) {
                ListingStatus.SOLD.name -> ListingSort.TRANSACTION_DATE
                else -> ListingSort.NEWEST
            },
            listingType = when (form.listingType) {
                ListingType.SALE.name -> ListingType.SALE
                ListingType.RENTAL.name -> ListingType.RENTAL
                else -> null
            },
            statuses = when (form.listingType) {
                ListingStatus.SOLD.name -> listOf(ListingStatus.SOLD, ListingStatus.RENTED)
                else -> listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES, ListingStatus.PENDING)
            },
            bedrooms = form.bedrooms.ifEmpty { null },
            bathrooms = form.bathrooms.ifEmpty { null },
            locationIds = form.locationIds,
            minPrice = form.minPrice,
            maxPrice = form.maxPrice,
            minLotArea = form.minLotArea,
            maxLotArea = form.maxLotArea,
            minPropertyArea = form.minPropertyArea,
            maxPropertyArea = form.maxPropertyArea,
        )

        if (form.locationIds.isNotEmpty()) {
            val locations = locationService.search(
                ids = form.locationIds,
                limit = form.locationIds.size
            )
            if (locations.isNotEmpty()) {
                model.addAttribute("locations", locations)
            }
        }

        model.addAttribute("listings", listings)
        model.addAttribute("form", form)
    }
}
