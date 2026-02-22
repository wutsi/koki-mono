package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.common.model.ResultSetModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListListingController(private val moneyMapper: MoneyMapper) : AbstractListingController() {
    @GetMapping
    fun list(
        @RequestParam(required = false) me: Boolean = true,
        @RequestParam(required = false, name = "location-id") locationId: Long? = null,

        @RequestParam(required = false) status: ListingStatus = ListingStatus.ACTIVE,
        @RequestParam(required = false, name = "property-category") propertyCategory: PropertyCategory? = null,
        @RequestParam(required = false, name = "listing-type") listingType: ListingType? = null,
        @RequestParam(required = false) bedrooms: String? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "sort-by") sortBy: ListingSort = ListingSort.PRICE_LOW_HIGH,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_LIST,
                title = getMessage("page.listing.list.meta.title"),
            )
        )

        val tenant = tenantHolder.get()
        model.addAttribute("country", tenant.country)
        if (locationId != null) {
            var location = locationService.get(locationId)
            if (location.type == LocationType.NEIGHBORHOOD) {
                val parent = location.parentId?.let { id -> locationService.get(id) }
                if (parent != null) {
                    location = location.copy(name = "${location.name}, ${parent.name}")
                }
            }
            model.addAttribute("locationId", locationId)
            model.addAttribute("location", location)
        }

        model.addAttribute("status", status)
        model.addAttribute(
            "statuses",
            ListingStatus.entries.filter {
                !listOf(
                    ListingStatus.UNKNOWN,
                    if (!me) ListingStatus.DRAFT else null,
                    ListingStatus.ACTIVE_WITH_CONTINGENCIES,
                    ListingStatus.PENDING,
                ).contains(it)
            }
        )

        model.addAttribute("propertyCategory", propertyCategory)
        model.addAttribute("propertyCategories", PropertyCategory.entries.filter { it != PropertyCategory.UNKNOWN })

        model.addAttribute("listingType", listingType)
        model.addAttribute("listingTypes", ListingType.entries.filter { it != ListingType.UNKNOWN })

        model.addAttribute("bedrooms", bedrooms)
        model.addAttribute("rooms", listOf("1", "1+", "2", "2+", "3", "3+", "4", "4+", "5", "5+"))

        model.addAttribute("sortBy", sortBy)
        model.addAttribute(
            "sortBys",
            listOf(
                ListingSort.PRICE_LOW_HIGH,
                ListingSort.PRICE_HIGH_LOW,
                ListingSort.NEWEST,
                ListingSort.OLDEST,
                ListingSort.TRANSACTION_DATE,
                ListingSort.MODIFIED_DATE,
            )
        )

        model.addAttribute(
            "maxPrice",
            maxPrice?.let { price ->
                moneyMapper.toMoneyModel(
                    Money(
                        amount = price.toDouble(),
                        currency = tenant.currency
                    )
                )
            }
        )
        loadPriceRange(
            me = me,
            status = status,
            listingType = listingType,
            propertyCategory = propertyCategory,
            locationId = locationId,
            bedrooms = bedrooms,
            model = model,
        )

        more(
            me = me,
            locationId = locationId,
            status = status,
            propertyCategory = propertyCategory,
            listingType = listingType,
            bedrooms = bedrooms,
            maxPrice = maxPrice,
            limit = 20,
            offset = 0,
            sortBy = sortBy,
            model = model,
        )
        return "listings/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) me: Boolean = true,
        @RequestParam(required = false, name = "location-id") locationId: Long? = null,
        @RequestParam(required = false) status: ListingStatus = ListingStatus.DRAFT,
        @RequestParam(required = false, name = "property-category") propertyCategory: PropertyCategory? = null,
        @RequestParam(required = false, name = "listing-type") listingType: ListingType? = null,
        @RequestParam(required = false) bedrooms: String? = null,
        @RequestParam(required = false, name = "max-price") maxPrice: Double? = null,
        @RequestParam(required = false, name = "sortBy") sortBy: ListingSort = ListingSort.PRICE_LOW_HIGH,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val listings = findListings(
            me = me,
            locationId,
            status = status,
            propertyCategory = propertyCategory,
            listingType = listingType,
            bedrooms = bedrooms,
            maxPrice = maxPrice,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
        )

        if (listings.items.isNotEmpty()) {
            model.addAttribute("listings", listings)
            if (listings.items.size >= limit) {
                val moreUrl = URLBuilder("")
                    .build(
                        "/listings/more",
                        mapOf(
                            "me" to me,
                            "location-id" to locationId,
                            "status" to status,
                            "property-category" to propertyCategory,
                            "listing-type" to listingType,
                            "bedrooms" to bedrooms,
                            "max-price" to maxPrice,
                            "sort-by" to sortBy,
                            "limit" to limit,
                            "offset" to offset + limit,
                        )
                    )
                model.addAttribute("moreUrl", moreUrl)
            }
        }

        model.addAttribute("me", me)
        model.addAttribute("sold", status == ListingStatus.SOLD || status == ListingStatus.RENTED)
        model.addAttribute("showCommission", false)
        return "listings/more"
    }

    private fun findListings(
        me: Boolean,
        locationId: Long?,
        status: ListingStatus,
        propertyCategory: PropertyCategory?,
        listingType: ListingType?,
        bedrooms: String?,
        maxPrice: Double?,
        sortBy: ListingSort,
        limit: Int,
        offset: Int
    ): ResultSetModel<ListingModel> {
        return listingService.search(
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            statuses = listOf(status),
            listingType = listingType,
            propertyCategories = propertyCategory?.let { listOf(propertyCategory) } ?: emptyList(),
            bedrooms = if (propertyCategory == null || propertyCategory == PropertyCategory.RESIDENTIAL) bedrooms else null,
            sellerAgentUserId = if (me) userHolder.get()?.id else null,
            maxPrice = maxPrice?.toLong(),
            limit = limit,
            offset = offset,
            sortBy = sortBy,
        )
    }

    private fun loadPriceRange(
        me: Boolean = true,
        status: ListingStatus,
        propertyCategory: PropertyCategory?,
        locationId: Long?,
        listingType: ListingType?,
        bedrooms: String?,
        model: Model
    ): Pair<MoneyModel, MoneyModel>? {
        var min = listingService.search(
            listingType = listingType,
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            propertyCategories = propertyCategory?.let { listOf(propertyCategory) } ?: emptyList(),
            sellerAgentUserId = if (me) userHolder.get()?.id else null,
            statuses = listOf(status),
            bedrooms = bedrooms,
            limit = 1,
            sortBy = ListingSort.PRICE_LOW_HIGH
        ).items.firstOrNull()?.price ?: return null
        min = adjustPrice(min, -.10)

        var max = listingService.search(
            listingType = listingType,
            locationIds = locationId?.let { listOf(locationId) } ?: emptyList(),
            propertyCategories = propertyCategory?.let { listOf(propertyCategory) } ?: emptyList(),
            sellerAgentUserId = if (me) userHolder.get()?.id else null,
            statuses = listOf(status),
            bedrooms = bedrooms,
            limit = 1,
            sortBy = ListingSort.PRICE_HIGH_LOW
        ).items.firstOrNull()?.price ?: return null

        max = adjustPrice(max, .10)
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
            amount = value.amount * (1.0 + percent),
            currency = value.currency,
        )
        return moneyMapper.toMoneyModel(money)
    }
}
