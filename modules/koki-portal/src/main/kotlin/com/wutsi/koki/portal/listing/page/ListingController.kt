package com.wutsi.koki.portal.listing.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequestMapping("/listings")
class ListingController : AbstractListingDetailsController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute(
            "composeUrl",
            "/message/compose&to-user-id=${listing.sellerAgentUser.id}&owner-id=$id&owner-type=${ObjectType.LISTING}"
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING,
                title = getMessage("page.listing.show.meta.title", arrayOf(listing.listingNumber)),
            )
        )
        return "listings/show"
    }

    @GetMapping("/{id}/details")
    fun details(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("amenityCategories", findAmenityCategories(listing))
        return "listings/details"
    }

    private fun findListing(id: Long): ListingModel {
        return ListingModel(
            id = id,
            listingNumber = "2024.1.00001",
            listingType = ListingType.SALE,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 4,
            bathrooms = 2,
            halfBathrooms = 1,
            basementType = BasementType.FULL,
            address = AddressModel(
                country = "CA",
                street = "340 Pascal",
                city = LocationModel(name = "Montreal", type = LocationType.CITY),
                state = LocationModel(name = "Quebec", type = LocationType.CITY),
                neighbourhood = LocationModel(name = "Mont Royal", type = LocationType.NEIGHBORHOOD),
                postalCode = "H7K 1C6",
                countryName = "Canada",
            ),
            geoLocation = GeoLocationModel(longitude = 45.506535014340116, latitude = -73.62631210301535),
            year = 1990,
            agentRemarks = "This is the remark of the agent",
            publicRemarks = null,
            price = MoneyModel(value = 1500.0, currency = "CA", text = "$1,500.00"),
            level = 1,
            floors = 3,
            unit = "303",
            propertyArea = 900,
            lotArea = 1200,
            parkingType = ParkingType.UNDERGROUND,
            parkings = 2,
            furnitureType = FurnitureType.FULLY_FURNISHED,
            description = "Excellent rapport qualité prix. A 30 mêtres de L'Avenue Mont-royal et 4 minutes du Métro, beau condo au RDC de 900 pc avec cachet et plafonds de 10 pieds. Deux chambres à coucher de bonne dimension dont une pièce double. Salle de bain refaite à neuf avec bain-douche séparé et munie d'un plancher chauffant. Usage exclusif de la terrasse arrière et jardin avant. Stationnement possible dans la rue avec vignette. Occupation rapide. (50804594)",
            amenities = listOf(
                AmenityModel(categoryId = 11, name = "Électricité"),
                AmenityModel(categoryId = 11, name = "Eau courante"),
                AmenityModel(categoryId = 22, name = "Réfrigérateur"),
                AmenityModel(categoryId = 22, name = "Micro-ondes"),
                AmenityModel(categoryId = 22, name = "Four"),
                AmenityModel(categoryId = 22, name = "Table a manger"),
                AmenityModel(categoryId = 33, name = "TV"),
                AmenityModel(categoryId = 33, name = "Câble/Satellite"),
                AmenityModel(categoryId = 33, name = "Jeux de société"),
                AmenityModel(categoryId = 33, name = "Services de streaming"),
            ),
            leaseTerm = 12,
            securityDeposit = MoneyModel(value = 4500.0, currency = "CA", text = "$45,000"),
            sellerName = "RAY SPONSIBLE",
            sellerEmail = "ray.sponsible@gmail.com",
            sellerPhone = "+15147580011",
            contractStartDate = LocalDate.now().minusDays(10),
            contractEndDate = LocalDate.now().plusMonths(6),
            contractRemarks = "This is the contractual remarks",
            sellerAgentCommission = 6.0,
            buyerAgentCommission = 2.5,
            sellerAgentUser = UserModel(
                id = 333,
                displayName = "Ray Sponsible",
                employer = "Courtier Immobilier SARL",
                phone = "+15147580100",
                photoUrl = "https://picsum.photos/128/128"
            ),
            status = ListingStatus.ACTIVE,
            fenceType = FenceType.CONCRETE,
            daysInMarket = 15,
            publicUrl = "https://www.realtor.ca/immobilier/28714279/5750-rue-carriere-brossard-noms-de-rues-c#view=neighbourhood"
        )
    }

    private fun findAmenityCategories(listing: ListingModel): List<CategoryModel> {
        return listOf(
            CategoryModel(id = 11, name = "Essentiels de base"),
            CategoryModel(id = 22, name = "Cuisine et salle à manger"),
            CategoryModel(id = 33, name = "Divertissement et électronique"),
        )
    }
}
