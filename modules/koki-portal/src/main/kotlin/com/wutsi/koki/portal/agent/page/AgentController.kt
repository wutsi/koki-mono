package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.agent.model.AgentMetricModel
import com.wutsi.koki.portal.agent.model.AgentMetricSetModel
import com.wutsi.koki.portal.agent.model.AgentModel
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Date

@Controller
@RequestMapping("/agents")
@RequiresPermission(["agent"])
class AgentController : AbstractAgentController() {
    @GetMapping("/{id}")
    fun list(@PathVariable id: Long, model: Model): String {
        val agent = findAgent(id)
        val listings = findListings(agent)
        model.addAttribute("agent", agent)
        model.addAttribute("listings", listings)
        model.addAttribute("mapCity", toMapCity(listings))
        model.addAttribute("mapMarkersJson", toMapMarkersJson(listings))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT,
                title = agent.user.displayName ?: "",
            )
        )
        return "agents/show"
    }

    private fun findAgent(id: Long): AgentModel {
        return AgentModel(
            id = id,
            user = UserModel(
                displayName = "Roger Milla",
                employer = "ZILLOW",
                photoUrl = "https://picsum.photos/600/600",
                biography = """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut.
                    Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                    Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                """.trimIndent(),
                facebookUrl = "https://www.facebook.com/tiktok",
                twitterUrl = "https://www.x.com/tiktok",
                tiktokUrl = "https://www.tiktok.com/facebook",
                websiteUrl = "https://www.google.com",
                instagramUrl = "https://www.instagram.com/tiktok",
                mobile = "+15147589999",
                mobileText = "(514) 758 99 99",
                email = "ray.sponsible@gmail.com",
                country = "ca",
                language = "fr",
                city = LocationModel(name = "Montreal"),
                category = CategoryModel(name = "Real Estate Agent"),
                languageText = "French",
                whatsappUrl = "https://www.wa.com/15147589999"
            ),
            pyMetrics = AgentMetricSetModel(
                sales = AgentMetricModel(
                    total = 121,
                    minPrice = MoneyModel(shortText = "$50K"),
                    maxPrice = MoneyModel(shortText = "$1M")
                ),
                rentals = AgentMetricModel(
                    total = 11,
                    minPrice = MoneyModel(shortText = "$500"),
                    maxPrice = MoneyModel(shortText = "$3K")
                ),
            ),
            metrics = AgentMetricSetModel(
                sales = AgentMetricModel(
                    total = 1215,
                    minPrice = MoneyModel(shortText = "$50K"),
                    maxPrice = MoneyModel(shortText = "$1M")
                ),
                rentals = AgentMetricModel(
                    total = 131,
                    minPrice = MoneyModel(shortText = "$1K"),
                    maxPrice = MoneyModel(shortText = "$7K")
                ),
            ),
        )
    }

    private fun findListings(agent: AgentModel): List<ListingModel> {
        return listOf(
            ListingModel(
                id = 10,
                status = ListingStatus.SOLD,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.APARTMENT,
                bedrooms = 2,
                bathrooms = 1,
                propertyArea = 900,
                address = AddressModel(
                    country = "CA",
                    street = "340 Pascal",
                    city = LocationModel(
                        id = 11,
                        name = "Montreal",
                        type = LocationType.CITY,
                        latitude = 45.50884,
                        longitude = -73.58781
                    ),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Mont Royal", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                geoLocation = GeoLocationModel(
                    latitude = 45.61667817096052, longitude = -73.62237035882583
                ),
                price = MoneyModel(amount = 250000.0, currency = "CA", displayText = "$250,000.00"),
                transactionPrice = MoneyModel(
                    amount = 1500.0,
                    currency = "CA",
                    displayText = "$250,000.00",
                    text = "$250,000.00"
                ),
                transactionDate = Date(),
                transactionDateText = "25 Oct 2020",
                sellerAgentUser = agent.user,
                heroImageUrl = "https://picsum.photos/600/600",
                transactionParty = OfferParty.SELLER,
            ),
            ListingModel(
                id = 11,
                status = ListingStatus.RENTED,
                listingNumber = "24709709",
                listingType = ListingType.RENTAL,
                propertyType = PropertyType.HOUSE,
                bedrooms = 4,
                bathrooms = 2,
                halfBathrooms = 1,
                propertyArea = 900,
                lotArea = 1200,
                address = AddressModel(
                    country = "CA",
                    street = "3400 Nicolet",
                    city = LocationModel(
                        id = 11,
                        name = "Montreal",
                        type = LocationType.CITY,
                        latitude = 45.50884,
                        longitude = -73.58781
                    ),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Verdun", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                geoLocation = GeoLocationModel(
                    latitude = 45.549788270959056, longitude = -73.55492361465114
                ),
                price = MoneyModel(amount = 1500.0, currency = "CA", displayText = "$1500,00 par mois"),
                transactionPrice = MoneyModel(
                    amount = 1500.0,
                    currency = "CA",
                    displayText = "$1500.00 par mois",
                    text = "$1500.00"
                ),
                transactionDate = Date(),
                transactionDateText = "21 Jan 2010",
                heroImageUrl = "https://picsum.photos/600/600",
                sellerAgentUser = agent.user,
                transactionParty = OfferParty.BUYER,
            ),

            ListingModel(
                id = 20,
                status = ListingStatus.SOLD,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.LAND,
                lotArea = 4000,
                address = AddressModel(
                    country = "CA",
                    street = "340 Pascal",
                    city = LocationModel(
                        id = 22,
                        name = "Laval",
                        type = LocationType.CITY,
                        latitude = 45.551538,
                        longitude = -73.744616
                    ),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Auteuil", type = LocationType.NEIGHBORHOOD),
                    postalCode = "H1K 1X6",
                    countryName = "Canada",
                ),
                geoLocation = GeoLocationModel(
                    latitude = 45.62737503173399, longitude = -73.74655306100279
                ),
                price = MoneyModel(amount = 40000.0, currency = "CA", displayText = "$40,000.00"),
                transactionPrice = MoneyModel(
                    amount = 40000.0,
                    currency = "CA",
                    displayText = "$40,00.00",
                    text = "$40,00.00"
                ),
                transactionDate = Date(),
                transactionDateText = "25 Sept 2020",
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 2,
                buyerAgentUser = agent.user,
                transactionParty = OfferParty.BUYER,
            ),
            ListingModel(
                id = 21,
                status = ListingStatus.SOLD,
                listingNumber = "24709709",
                listingType = ListingType.SALE,
                propertyType = PropertyType.STUDIO,
                bedrooms = 1,
                bathrooms = 1,
                propertyArea = 250,
                address = AddressModel(
                    country = "CA",
                    street = "3030 Linton",
                    city = LocationModel(
                        id = 11,
                        name = "Montreal",
                        type = LocationType.CITY,
                        latitude = 45.50884,
                        longitude = -73.58781
                    ),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Cote-des-Neiges", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                geoLocation = GeoLocationModel(
                    latitude = 45.50642974729282, longitude = -73.62619408581763
                ),
                price = MoneyModel(amount = 149000.0, currency = "CA", displayText = "$149,000.00"),
                transactionPrice = MoneyModel(amount = 150000.0, currency = "CA", displayText = "$150,00.00"),
                transactionDate = Date(),
                transactionDateText = "25 Oct 2020",
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 1,
                buyerAgentUser = agent.user,
                transactionParty = OfferParty.SELLER,
            ),
            ListingModel(
                id = 21,
                status = ListingStatus.RENTED,
                listingNumber = "24709709",
                listingType = ListingType.RENTAL,
                propertyType = PropertyType.STUDIO,
                bedrooms = 1,
                bathrooms = 1,
                propertyArea = 250,
                address = AddressModel(
                    country = "CA",
                    street = "700 Saint Alexandre",
                    city = LocationModel(
                        id = 11,
                        name = "Montreal",
                        type = LocationType.CITY,
                        latitude = 45.50884,
                        longitude = -73.58781
                    ),
                    state = LocationModel(name = "Quebec", type = LocationType.CITY),
                    neighbourhood = LocationModel(name = "Cote-des-Neiges", type = LocationType.NEIGHBORHOOD),
                    countryName = "Canada",
                ),
                geoLocation = GeoLocationModel(
                    latitude = 45.502287336006646, longitude = -73.56172666289113
                ),
                price = MoneyModel(amount = 750.0, currency = "CA", displayText = "$750.00"),
                transactionPrice = MoneyModel(amount = 750.0, currency = "CA", displayText = "$750.00"),
                transactionDate = Date(),
                transactionDateText = "25 Oct 2025",
                heroImageUrl = "https://picsum.photos/600/600",
                totalActiveMessages = 7,
                buyerAgentUser = agent.user,
                transactionParty = OfferParty.SELLER,
            ),
        )
    }

    private fun toMapMarkersJson(listings: List<ListingModel>): String? {
        val beds = getMessage("page.listing.bedrooms-abbreviation")
        val markers = listings.map { listing ->
            mapOf(
                "id" to listing.id,
                "rental" to (listing.listingType == ListingType.RENTAL),
                "latitude" to listing.geoLocation?.latitude,
                "longitude" to listing.geoLocation?.longitude,
                "price" to listing.transactionPrice?.displayText,
                "heroImageUrl" to listing.heroImageUrl,
                "bedrooms" to (listing.bedrooms?.toString() ?: "--") + " " + beds,
                "area" to ((listing.lotArea?.let { listing.propertyArea }?.toString() ?: "--") + "m2"),
                "url" to "/listings/${listing.id}",
            )
        }
        return objectMapper.writeValueAsString(markers)
    }

    fun toMapCity(listings: List<ListingModel>): LocationModel? {
        val cities = listings.mapNotNull { listing -> listing.address?.city }
            .distinctBy { city -> city.id }

        val cityCount = listings
            .mapNotNull { listing -> listing.address?.city }
            .groupBy { city -> city.id }

        return cities.sortedBy { city -> cityCount[city.id]?.size ?: 0 }
            .lastOrNull()
    }
}
