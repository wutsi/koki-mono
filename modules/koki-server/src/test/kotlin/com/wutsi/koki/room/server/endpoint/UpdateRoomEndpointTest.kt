package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.room.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/UpdateRoomEndpoint.sql"])
class UpdateRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomRepository

    @Test
    fun update() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UDT")

        val request = UpdateRoomRequest(
            title = "Beautiful Cottage",
            description = "This is a nice cottage located in Yaounde",
            summary = "This is a nice summary",
            maxGuests = 7,
            numberOfRooms = 4,
            numberOfBathrooms = 2,
            numberOfBeds = 8,
            area = 1000,
            street = "3030 Pascal",
            postalCode = "H1H13kj",
            cityId = 2001,
            neighborhoodId = 200111,
            pricePerNight = 75.0,
            pricePerMonth = 500.0,
            currency = "CAD",
            checkoutTime = "15:00",
            checkinTime = "12:00",
            leaseTerm = LeaseTerm.WEEKLY,
            leaseType = LeaseType.LONG_TERM,
            categoryId = 777L,
            furnishedType = FurnishedType.FULLY_FURNISHED,
            leaseTermDuration = 12,
            visitFees = 5.0,
            yearOfConstruction = 2000,
            dateOfAvailability = Date(),
        )
        val response = rest.postForEntity("/v1/rooms/111", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(111).get()

        assertEquals(USER_ID, room.modifiedById)
        assertEquals(33L, room.accountId)
        assertEquals(request.title, room.title)
        assertEquals(request.description, room.description)
        assertEquals(request.summary, room.summary)
        assertEquals(request.maxGuests, room.maxGuests)
        assertEquals(request.numberOfRooms, room.numberOfRooms)
        assertEquals(request.numberOfBathrooms, room.numberOfBathrooms)
        assertEquals(request.numberOfBeds, room.numberOfBeds)
        assertEquals(request.area, room.area)
        assertEquals(request.street, room.street)
        assertEquals(request.postalCode, room.postalCode)
        assertEquals(request.cityId, room.cityId)
        assertEquals(200, room.stateId)
        assertEquals("CA", room.country)
        assertEquals(request.pricePerNight, room.pricePerNight)
        assertEquals(request.pricePerMonth, room.pricePerMonth)
        assertEquals(request.currency, room.currency)
        assertEquals(request.checkinTime, room.checkinTime)
        assertEquals(request.checkoutTime, room.checkoutTime)
        assertEquals(request.neighborhoodId, room.neighborhoodId)
        assertEquals(request.leaseTerm, room.leaseTerm)
        assertEquals(request.leaseType, room.leaseType)
        assertEquals(request.furnishedType, room.furnishedType)
        assertEquals(request.categoryId, room.categoryId)
        assertEquals(request.leaseTermDuration, room.leaseTermDuration)
        assertEquals(request.visitFees, room.visitFees)
        assertEquals(request.yearOfConstruction, room.yearOfConstruction)
        assertEquals(fmt.format(request.dateOfAvailability), fmt.format(room.dateOfAvailability?.time))
    }
}
