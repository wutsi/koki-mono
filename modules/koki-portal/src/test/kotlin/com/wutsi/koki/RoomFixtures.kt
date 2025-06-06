package com.wutsi.koki

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.RoomUnit
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.RoomUnitSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object RoomFixtures {
    val rooms = listOf(
        RoomSummary(
            id = 1111,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Classic Double Room",
            summary = "Nice double room",
            heroImageId = FileFixtures.images[0].id,
            maxGuests = 2,
            numberOfBeds = 1,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 75.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            ),
            listingUrl = "/rooms/1111/classic-double-room",
        ),
        RoomSummary(
            id = 1112,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Comfort Single Room",
            summary = "Comfortable room with stunning view",
            heroImageId = FileFixtures.images[1].id,
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 100.0, currency = "CAD"),
            pricePerMonth = Money(amount = 1500.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            ),
            listingUrl = "/rooms/1112/classic-single-room",
        ),
        RoomSummary(
            id = 1113,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Deluxe Double Room",
            heroImageId = FileFixtures.images[2].id,
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 150.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            ),
            listingUrl = "/rooms/1113/deluxe-double-room",
        ),
        RoomSummary(
            id = 1114,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Standard Single Room, 1 Queen Bed",
            maxGuests = 2,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 100.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
            ),
            listingUrl = "/rooms/1114/standard-single-room-1-queen-bed",
        ),
        RoomSummary(
            id = 1115,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHING,
            type = RoomType.ROOM,
            title = "Presidential Suite",
            maxGuests = 6,
            numberOfBeds = 3,
            numberOfBathrooms = 3,
            numberOfRooms = 3,
            pricePerNight = Money(amount = 500.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
            ),
            listingUrl = null
        ),
        RoomSummary(
            id = 1116,
            accountId = AccountFixtures.accounts[0].id,
            status = RoomStatus.PUBLISHED,
            type = RoomType.APARTMENT,
            title = "Windmill in Ponta Delgada",
            maxGuests = 6,
            numberOfBeds = 3,
            numberOfBathrooms = 3,
            numberOfRooms = 3,
            pricePerNight = Money(amount = 350.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
                stateId = RefDataFixtures.cities[1].parentId,
                street = "3030 Linton",
            ),
            listingUrl = "/rooms/1116/windmill-in-ponta-delgada",
        ),
    )

    val room = Room(
        id = 1115,
        accountId = AccountFixtures.accounts[0].id,
        status = RoomStatus.DRAFT,
        type = RoomType.APARTMENT,
        title = "Windmill in Ponta Delgada",
        description = """
            Built in the 19th century, with a 360 degrees view over the sea and surroundings on the top floor.
            It features a Bedroom, a very well-decorated living room with kitchenette, and a WC.
            Free WiFi, air conditioning, Led TV and DVD player.
            Private parking inside the premises, providing extra security.
            Perfect for an unforgettable honeymoon experience.
        """.trimIndent(),
        summary = "Built in the 19th century, with 3 bed rooms, 2 bathroom an a stunning view",
        heroImageId = FileFixtures.images[0].id,
        maxGuests = 6,
        numberOfBeds = 3,
        numberOfBathrooms = 2,
        numberOfRooms = 1,
        pricePerNight = Money(amount = 55.0, currency = "CAD"),
        pricePerMonth = Money(amount = 1500.0, currency = "CAD"),
        address = Address(
            cityId = RefDataFixtures.cities[0].id,
            country = RefDataFixtures.cities[0].country,
            stateId = RefDataFixtures.cities[0].parentId,
            street = "3030 Linton",
            postalCode = "H1X 1X1",
        ),
        neighborhoodId = RefDataFixtures.neighborhoods[0].id,
        createdById = UserFixtures.USER_ID,
        modifiedById = UserFixtures.USER_ID,
        amenityIds = listOf(
            RefDataFixtures.amenities[0].id,
            RefDataFixtures.amenities[1].id,
            RefDataFixtures.amenities[2].id,
            RefDataFixtures.amenities[3].id,
        ),
        checkinTime = "11:00",
        checkoutTime = "15:00",
        latitude = 45.50654997823367,
        longitude = -73.62629065845323,
        leaseTerm = LeaseTerm.YEARLY,
        leaseType = LeaseType.SHORT_TERM,
        furnishedType = FurnishedType.FULLY_FURNISHED,
        area = 750,
        categoryId = RefDataFixtures.categories[0].id,
        listingUrl = "/rooms/1116/windmill-in-ponta-delgada",
        yearOfConstruction = 2010,
        advanceRent = 6,
        leaseTermDuration = 12,
        visitFees = Money(5.0, "CAD"),
        dateOfAvailability = DateUtils.addMonths(Date(), 6),
    )

    val roomUnits = listOf(
        RoomUnitSummary(
            id = 100,
            roomId = rooms[0].id,
            floor = 1,
            number = "100",
            status = RoomUnitStatus.AVAILABLE,
        ),
        RoomUnitSummary(
            id = 101,
            roomId = rooms[0].id,
            floor = 1,
            number = "101",
            status = RoomUnitStatus.AVAILABLE,
        ),
        RoomUnitSummary(
            id = 102,
            roomId = rooms[0].id,
            floor = 1,
            number = "102",
            status = RoomUnitStatus.AVAILABLE,
        ),
        RoomUnitSummary(
            id = 200,
            roomId = rooms[0].id,
            floor = 2,
            number = "200",
            status = RoomUnitStatus.UNDER_MAINTENANCE,
        ),
        RoomUnitSummary(
            id = 201,
            roomId = rooms[0].id,
            floor = 2,
            number = "201",
            status = RoomUnitStatus.UNDER_MAINTENANCE,
        ),
    )

    val roomUnit = RoomUnit(
        id = 201,
        roomId = rooms[0].id,
        floor = 2,
        number = "201",
        status = RoomUnitStatus.UNDER_MAINTENANCE,
        createdAt = DateUtils.addDays(Date(), -5),
        createdById = UserFixtures.USER_ID,
        modifiedAt = DateUtils.addDays(Date(), -1),
        modifiedById = UserFixtures.USER_ID,
    )
}
