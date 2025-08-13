package com.wutsi.koki.portal.listing.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingFilterForm
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListListingController : AbstractListingController() {
    @GetMapping
    fun list(@ModelAttribute form: ListingFilterForm, model: Model): String {
        val listings = loadListings()
        if (!form.isEmpty()) {
            model.addAttribute("form", form)
        }
        return list(listings, model)
    }

    @GetMapping("/filter")
    fun filter(model: Model): String {
        model.addAttribute("form", ListingFilterForm())
        model.addAttribute("listingTypes", ListingType.entries)
        model.addAttribute("propertyTypes", PropertyType.entries)
        model.addAttribute("rooms", listOf("1", "1+", "2", "2+", "3", "3+", "4", "4+"))

        return "listings/filter"
    }

    @PostMapping
    fun search(@ModelAttribute form: ListingFilterForm, model: Model): String {
        model.addAttribute("form", form)

        val listings = loadListings()
        return list(listings, model)
    }

    private fun list(listings: List<ListingModel>, model: Model): String {
        model.addAttribute("listings", listings.sortedBy { listing -> listing.status.ordinal })

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_LIST,
                title = getMessage("page.listing.list.meta.title"),
            )
        )
        return "listings/list"
    }

    private fun loadListings(): List<ListingModel> {
        return listOf(
            ListingModel(
                id = 10,
                status = ListingStatus.DRAFT,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.APARTMENT,
                bedrooms = 2,
                bathrooms = 1,
                propertyArea = 900,
                address = AddressModel(
                    country = "CA",
                    street = "340 Pascal",
                    city = LocationModel(name = "Montreal", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Mont Royal", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 250000.0, currency = "CA", text = "$250,000.00"),
                heroImageUrl = "https://picsum.photos/600/600",
            ),
            ListingModel(
                id = 11,
                status = ListingStatus.DRAFT,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.HOUSE,
                bedrooms = 4,
                bathrooms = 2,
                halfBathrooms = 1,
                propertyArea = 900,
                lotArea = 1200,
                address = AddressModel(
                    country = "CA",
                    street = "3400 Nicolet",
                    city = LocationModel(name = "Montreal", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Verdun", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 1500.0, currency = "CA", text = "$1500,00"),
                heroImageUrl = "https://picsum.photos/600/600",
            ),

            ListingModel(
                id = 20,
                status = ListingStatus.ACTIVE,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.LAND,
                lotArea = 4000,
                address = AddressModel(
                    country = "CA",
                    street = "340 Pascal",
                    city = LocationModel(name = "Laval", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Auteuil", type = LocationType.NEIGHBORHOOD),
                    postalCode = "H1K 1X6",
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 40000.0, currency = "CA", text = "$40,000.00"),
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 2,
            ),
            ListingModel(
                id = 21,
                status = ListingStatus.ACTIVE,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.STUDIO,
                bedrooms = 1,
                bathrooms = 1,
                propertyArea = 250,
                address = AddressModel(
                    country = "CA",
                    street = "3030 Linton",
                    city = LocationModel(name = "Montreal", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Cote-des-Neiges", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 149000.0, currency = "CA", text = "$149,000.00"),
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 1,
            ),
            ListingModel(
                id = 21,
                status = ListingStatus.ACTIVE,
                listingNumber = "24709709",
                listingType = ListingType.RENTAL,
                propertyType = PropertyType.STUDIO,
                bedrooms = 1,
                bathrooms = 1,
                propertyArea = 250,
                address = AddressModel(
                    country = "CA",
                    street = "3030 Linton",
                    city = LocationModel(name = "Montreal", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Cote-des-Neiges", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 750.0, currency = "CA", text = "$750.00"),
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 7,
            ),

            ListingModel(
                id = 30,
                status = ListingStatus.ACTIVE_WITH_OFFER,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.HOUSE,
                bedrooms = 5,
                bathrooms = 3,
                propertyArea = 1500,
                lotArea = 2000,
                address = AddressModel(
                    country = "CA",
                    street = "340 Pascal",
                    city = LocationModel(name = "Saint-Isidore", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Secteur 3", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 1250000.0, currency = "CA", text = "$1,250,000.00"),
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 2,
            ),

            ListingModel(
                id = 30,
                status = ListingStatus.PENDING,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.STUDIO,
                bedrooms = 1,
                bathrooms = 1,
                propertyArea = 350,
                address = AddressModel(
                    country = "CA",
                    street = "340 Peladeau",
                    city = LocationModel(name = "Laval", type = LocationType.CITY),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Vimon", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                price = MoneyModel(value = 220000.0, currency = "CA", text = "$220,000.00"),
                heroImageUrl = "https://picsum.photos/600/600",
            ),
        )
    }
}
