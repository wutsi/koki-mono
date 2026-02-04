package com.wutsi.koki.portal.pub.search.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.portal.pub.common.mapper.MoneyMapper
import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.portal.pub.common.model.ResultSetModel
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/search")
class SearchController(
    private val listingService: ListingService,
    private val locationService: LocationService,
    private val moneyMapper: MoneyMapper,
) : AbstractPageController() {
    companion object {
        const val LIMIT = 12
    }

    @GetMapping
    fun result(
        @RequestParam(name = "location-id", required = false) locationId: Long? = null,
        @RequestParam(name = "property-category", required = false) propertyCategory: String? = null,
        @RequestParam(name = "listing-type", required = false) listingType: String = ListingType.RENTAL.name,
        @RequestParam(required = false) bedrooms: String? = null,
        @RequestParam(required = false, name = "min-price") minPrice: Double? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "sort") sort: String? = null,
        model: Model,
    ): String {
        // Data
        val tenant = tenantHolder.get()
        loadListings(
            locationId = locationId,
            listingType = listingType,
            propertyCategory = propertyCategory,
            bedrooms = bedrooms,
            minPrice = minPrice,
            maxPrice = maxPrice,
            offset = 0,
            sort = sort,
            model = model
        )
        loadPriceRange(
            propertyCategory = propertyCategory,
            locationId = locationId,
            listingType = listingType,
            bedrooms = bedrooms,
            model = model
        )

        // Filters
        val location = locationId?.let { id ->
            var location = locationService.get(id)
            if (location.type == LocationType.NEIGHBORHOOD) {
                val parent = location.parentId?.let { id -> locationService.get(id) }
                if (parent != null) {
                    location = location.copy(name = "${location.name}, ${parent.name}")
                }
            }
            location
        }
        model.addAttribute("locationId", locationId)
        model.addAttribute("location", location)
        model.addAttribute("propertyCategory", toPropertyCategory(propertyCategory))
        model.addAttribute("listingType", toListingType(listingType))
        model.addAttribute("bedrooms", bedrooms?.ifEmpty { null })
        model.addAttribute("minPrice", minPrice?.let { moneyMapper.toMoneyModel(minPrice, tenant.currency) })
        model.addAttribute("maxPrice", maxPrice?.let { moneyMapper.toMoneyModel(maxPrice, tenant.currency) })
        model.addAttribute("sort", toListingSort(sort))

        model.addAttribute("listingTypes", ListingType.entries.filter { it != ListingType.UNKNOWN })
        model.addAttribute("propertyCategories", PropertyCategory.entries.filter { it != PropertyCategory.UNKNOWN })
        model.addAttribute("rooms", listOf("1", "1+", "2", "2+", "3", "3+", "4", "4+", "5", "5+"))
        model.addAttribute(
            "sortBys",
            listOf(ListingSort.NEWEST, ListingSort.PRICE_LOW_HIGH, ListingSort.PRICE_HIGH_LOW)
        )
        model.addAttribute("country", tenant.country)

        // Page
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SEARCH,
                title = if (location == null) {
                    getMessage("listing-type.$listingType")
                } else if (listingType.uppercase() == ListingType.RENTAL.name) {
                    getMessage("page.search.meta.title-for-rent", arrayOf(location.name))
                } else {
                    getMessage("page.search.meta.title-for-sale", arrayOf(location.name))
                },
            ),
        )
        return "search/result"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "property-category", required = false) propertyCategory: String? = null,
        @RequestParam(name = "location-id", required = false) locationId: Long? = null,
        @RequestParam(name = "listing-type", required = false) listingType: String? = null,
        @RequestParam(required = false) bedrooms: String? = null,
        @RequestParam(required = false, name = "min-price") minPrice: Double? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "sort") sort: String? = null,
        offset: Int,
        model: Model,
    ): String {
        loadListings(
            locationId = locationId,
            listingType = listingType,
            propertyCategory = propertyCategory,
            bedrooms = bedrooms,
            minPrice = minPrice,
            maxPrice = maxPrice,
            offset = offset,
            sort = sort,
            model = model
        )
        return "search/more"
    }

    private fun loadListings(
        propertyCategory: String?,
        locationId: Long?,
        listingType: String?,
        bedrooms: String?,
        minPrice: Double?,
        maxPrice: Double?,
        sort: String?,
        offset: Int,
        model: Model,
    ): ResultSetModel<ListingModel> {
        val bedroomPair = minmaxPair(bedrooms)
        val listings = listingService.search(
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            listingType = toListingType(listingType),
            propertyCategories = toPropertyCategory(propertyCategory)?.let { it -> listOf(it) } ?: emptyList(),
            minBedrooms = bedroomPair.first,
            maxBedrooms = bedroomPair.second,
            statuses = listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES),
            minPrice = minPrice?.toLong(),
            maxPrice = maxPrice?.toLong(),
            limit = LIMIT,
            offset = offset,
            sortBy = toListingSort(sort)
        )

        if (!listings.isEmpty()) {
            model.addAttribute("listings", listings)

            if (listings.items.size >= LIMIT) {
                val urlBuilder = URLBuilder("")
                val moreUrl = urlBuilder.build(
                    "/search/more",
                    mapOf(
                        "property-category" to propertyCategory,
                        "listing-type" to toListingType(listingType),
                        "location-id" to locationId,
                        "listing-type" to listingType,
                        "bedrooms" to bedrooms,
                        "min-price" to minPrice,
                        "max-price" to maxPrice,
                        "sort" to sort,
                        "offset" to (offset + LIMIT),
                    )
                )
                model.addAttribute("moreUrl", moreUrl)
            }
        }
        return listings
    }

    private fun loadPriceRange(
        propertyCategory: String? = null,
        locationId: Long? = null,
        listingType: String? = null,
        bedrooms: String? = null,
        model: Model
    ): Pair<MoneyModel, MoneyModel>? {
        var min: MoneyModel
        var max: MoneyModel

        val bedroomPair = minmaxPair(bedrooms)
        val propertyCategories = toPropertyCategory(propertyCategory)?.let { it -> listOf(it) } ?: emptyList()
        min = listingService.search(
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            propertyCategories = propertyCategories,
            listingType = toListingType(listingType),
            statuses = listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES),
            limit = 1,
            sortBy = ListingSort.PRICE_LOW_HIGH
        ).items.firstOrNull()?.price ?: return null

        max = listingService.search(
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            listingType = toListingType(listingType),
            propertyCategories = propertyCategories,
            minBedrooms = bedroomPair.first,
            maxBedrooms = bedroomPair.second,
            statuses = listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES),
            limit = 1,
            sortBy = ListingSort.PRICE_HIGH_LOW
        ).items.firstOrNull()?.price ?: return null

        max = adjustPrice(max, .25)
        val currencySymbol = max.shortText.split(" ")[0]

        val step = if (min.amount / 10000L > 0) {
            1000
        } else {
            100
        }

        model.addAttribute("priceRangeMin", min)
        model.addAttribute("priceRangeMax", max)
        model.addAttribute("priceRangeStep", step)
        model.addAttribute("currencySymbol", currencySymbol)

        return Pair(min, max)
    }

    private fun adjustPrice(value: MoneyModel, percent: Double): MoneyModel {
        val money = Money(
            amount = value.amount * (1.0 + percent / 100.0),
            currency = value.currency,
        )
        return moneyMapper.toMoneyModel(money)
    }

    private fun minmaxPair(value: String?): Pair<Int?, Int?> {
        try {
            if (value.isNullOrEmpty()) {
                return Pair(null, null)
            } else if (value.endsWith("+")) {
                return Pair(value.trimEnd('+').toInt(), null)
            } else {
                val num = value.toInt()
                return Pair(num, num)
            }
        } catch (e: NumberFormatException) {
            return Pair(null, null)
        }
    }

    private fun toListingType(value: String?): ListingType? {
        return try {
            value?.let { ListingType.valueOf(it.uppercase()) }
        } catch (ex: Exception) {
            null
        }
    }

    private fun toPropertyCategory(value: String?): PropertyCategory? {
        return try {
            value?.let { PropertyCategory.valueOf(it.uppercase()) }
        } catch (ex: Exception) {
            null
        }
    }

    private fun toListingSort(value: String?): ListingSort? {
        return try {
            value?.let { ListingSort.valueOf(it.uppercase()) }
        } catch (ex: Exception) {
            null
        }
    }
}
