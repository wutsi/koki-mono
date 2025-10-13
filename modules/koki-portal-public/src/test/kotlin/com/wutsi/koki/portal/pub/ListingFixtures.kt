package com.wutsi.koki.portal.pub

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingSummary
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.portal.pub.ContactFixtures.contacts
import com.wutsi.koki.portal.pub.UserFixtures.USER_ID
import com.wutsi.koki.portal.pub.UserFixtures.users
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.refdata.dto.Money
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object ListingFixtures {
    val listing = Listing(
        id = 1115,
        listingNumber = 243001,
        status = ListingStatus.ACTIVE,
        listingType = ListingType.SALE,
        propertyType = PropertyType.APARTMENT,
        title = "House for sale, 3 bedrooms, Yaounde (Bastos)",
        summary = "Built in the 19th century, with a 360 degrees view over the sea and surroundings on the top floor",
        publicUrl = "/listings/1115/house-for-sale-3-bedrooms-yaounde-bastos",
        description = """
            Built in the 19th century, with a 360 degrees view over the sea and surroundings on the top floor.It features a Bedroom, a very well-decorated living room with kitchenette, and a WC.

            Free WiFi, air conditioning, Led TV and DVD player. Private parking inside the premises, providing extra security.

            Perfect for an unforgettable honeymoon experience.
        """.trimIndent(),
        titleFr = "Maison a vendre, 3 chambres, Yaounde (Bastos)",
        publicUrlFr = "/listings/1115/maison-a-louer-3-chambres-yaounde-bastos",
        summaryFr = "Construit au XIXe siècle, il offre une vue à 360 degrés sur la mer et les environs depuis le dernier étage",
        descriptionFr = """
            Construit au XIXe siècle, il offre une vue à 360 degrés sur la mer et les environs depuis le dernier étage.Il comprend une chambre, un salon très bien décoré avec kitchenette et des toilettes.

            Wi-Fi gratuit, climatisation, télévision LED et lecteur DVD. Parking privé à l'intérieur de la propriété, pour une sécurité accrue.

            Idéal pour une lune de miel inoubliable.
        """.trimIndent(),
        bedrooms = 3,
        bathrooms = 2,
        halfBathrooms = 1,
        basementType = BasementType.NONE,
        unit = "303",
        propertyArea = 1200,
        lotArea = 850,
        floors = 1,
        level = 10,
        parkingType = ParkingType.UNDERGROUND,
        parkings = 2,
        fenceType = FenceType.CONCRETE,
        year = 1990,
        availableAt = DateUtils.addDays(Date(), 15),
        distanceFromMainRoad = 200,
        roadPavement = RoadPavement.GRAVEL,

        furnitureType = FurnitureType.FULLY_FURNISHED,
        amenityIds = listOf(
            RefDataFixtures.amenities[0].id,
            RefDataFixtures.amenities[1].id,
            RefDataFixtures.amenities[2].id,
            RefDataFixtures.amenities[3].id,
        ),

        address = Address(
            cityId = RefDataFixtures.cities[0].id,
            country = RefDataFixtures.cities[0].country,
            stateId = RefDataFixtures.cities[0].parentId,
            neighborhoodId = RefDataFixtures.neighborhoods[0].id,
            street = "3030 Linton",
            postalCode = "H1X 1X1",
        ),

        geoLocation = GeoLocation(
            latitude = 45.50654997823367,
            longitude = -73.62629065845323,
        ),

        price = Money(175000.0, "CAD"),
        visitFees = Money(10.0, "CAD"),
        sellerAgentCommission = 6.0,
        buyerAgentCommission = 3.0,
        sellerAgentCommissionMoney = Money(40000.0, "CAD"),
        buyerAgentCommissionMoney = Money(20000.0, "CAD"),

        noticePeriod = 1,
        leaseTerm = 12,
        securityDeposit = Money(15000.0, "CAD"),
        advanceRent = 3,

        sellerContactId = contacts[0].id,

        publicRemarks = """
            These are public remarks
        """.trimIndent(),
        agentRemarks = """
            Agent remarks that are not public!
        """.trimIndent(),

        sellerAgentUserId = USER_ID,
        createdById = USER_ID,
        heroImageId = FileFixtures.images[0].id,
        totalFiles = 10,
        totalImages = 11,

        buyerAgentUserId = users[2].id,
        buyerContactId = contacts[1].id,
        transactionPrice = Money(195000.0, "CAD"),
        transactionDate = DateUtils.addDays(Date(), -10),
        finalSellerAgentCommissionMoney = Money(50000.0, "CAD"),
        finalBuyerAgentCommissionMoney = Money(22500.0, "CAD"),
    )

    val listings = listOf(
        ListingSummary(
            id = 1115,
            listingNumber = 243001,
            status = ListingStatus.ACTIVE,
            listingType = ListingType.SALE,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            propertyArea = 1200,
            lotArea = 850,
            furnitureType = FurnitureType.FULLY_FURNISHED,
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
                stateId = RefDataFixtures.cities[0].parentId,
                neighborhoodId = RefDataFixtures.neighborhoods[0].id,
                street = "3030 Linton",
                postalCode = "H1X 1X1",
            ),

            price = Money(175000.0, "CAD"),
            buyerAgentCommission = 3.0,

            sellerAgentUserId = UserFixtures.USER_ID,
            heroImageId = FileFixtures.images[0].id,

            buyerAgentUserId = UserFixtures.users[2].id,
            transactionPrice = Money(195000.0, "CAD"),
            transactionDate = DateUtils.addDays(Date(), -10)
        ),
        ListingSummary(
            id = 1116,
            listingNumber = 243002,
            status = ListingStatus.DRAFT,
            listingType = ListingType.SALE,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            propertyArea = 1200,
            lotArea = 850,
            furnitureType = FurnitureType.FULLY_FURNISHED,
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
                stateId = RefDataFixtures.cities[0].parentId,
                neighborhoodId = RefDataFixtures.neighborhoods[1].id,
                street = "340 Pascal",
                postalCode = null,
            ),

            price = Money(375000.0, "CAD"),
            buyerAgentCommission = 3.0,

            sellerAgentUserId = UserFixtures.USER_ID,
            heroImageId = FileFixtures.images[0].id,

            buyerAgentUserId = UserFixtures.users[2].id,
            transactionPrice = Money(195000.0, "CAD"),
            transactionDate = DateUtils.addDays(Date(), -10)
        ),
        ListingSummary(
            id = 1117,
            listingNumber = 243003,
            status = ListingStatus.PUBLISHING,
            listingType = ListingType.RENTAL,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            propertyArea = 1200,
            lotArea = 850,
            furnitureType = FurnitureType.FULLY_FURNISHED,
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
                stateId = RefDataFixtures.cities[0].parentId,
                neighborhoodId = RefDataFixtures.neighborhoods[1].id,
                street = "1111 Nicolet",
                postalCode = null,
            ),

            price = Money(1000.0, "CAD"),
            buyerAgentCommission = 3.0,

            sellerAgentUserId = UserFixtures.USER_ID,
            heroImageId = FileFixtures.images[0].id,

            buyerAgentUserId = UserFixtures.users[2].id,
            transactionPrice = Money(195000.0, "CAD"),
            transactionDate = DateUtils.addDays(Date(), -1)
        ),
        ListingSummary(
            id = 1117,
            listingNumber = 243003,
            status = ListingStatus.ACTIVE,
            listingType = ListingType.SALE,
            propertyType = PropertyType.LAND,
            lotArea = 1200,
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
                stateId = RefDataFixtures.cities[0].parentId,
                neighborhoodId = RefDataFixtures.neighborhoods[1].id,
                street = "1111 Nicolet",
                postalCode = null,
            ),

            price = Money(1000.0, "CAD"),
            buyerAgentCommission = 1.0,

            sellerAgentUserId = UserFixtures.users[1].id,
            heroImageId = FileFixtures.images[0].id,

            buyerAgentUserId = UserFixtures.USER_ID,
            transactionPrice = Money(195000.0, "CAD"),
            transactionDate = DateUtils.addDays(Date(), -1)
        )
    )
}
