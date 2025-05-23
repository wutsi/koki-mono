package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/CreateRoomEndpoint.sql"])
class CreateRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomRepository

    @Test
    fun create() {
        val request = CreateRoomRequest(
            type = RoomType.APARTMENT,
            maxGuests = 7,
            numberOfRooms = 4,
            numberOfBathrooms = 2,
            numberOfBeds = 8,
            area = 500,
            street = "3030 Pascal",
            postalCode = "H1H13kj",
            cityId = 1001,
            neighborhoodId = 100111,
            pricePerNight = 35.0,
            pricePerMonth = 500.0,
            currency = "CAD",
            checkoutTime = "15:00",
            checkinTime = "12:00",
            leaseTerm = LeaseTerm.YEARLY,
            leaseType = LeaseType.LONG_TERM,
            categoryId = 777L,
            furnishedType = FurnishedType.FULLY_FURNISHED,
        )
        val response = rest.postForEntity("/v1/rooms", request, CreateRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val roomId = response.body!!.roomId
        val room = dao.findById(roomId).get()

        assertEquals(TENANT_ID, room.tenantId)
        assertEquals(RoomStatus.DRAFT, room.status)
        assertEquals(USER_ID, room.createdById)
        assertEquals(USER_ID, room.modifiedById)
        assertEquals(request.type, room.type)
        assertEquals(null, room.title)
        assertEquals(null, room.description)
        assertEquals(request.maxGuests, room.maxGuests)
        assertEquals(request.numberOfRooms, room.numberOfRooms)
        assertEquals(request.numberOfBathrooms, room.numberOfBathrooms)
        assertEquals(request.numberOfBeds, room.numberOfBeds)
        assertEquals(request.area, room.area)
        assertEquals(request.street, room.street)
        assertEquals(request.postalCode, room.postalCode)
        assertEquals(request.cityId, room.cityId)
        assertEquals(request.neighborhoodId, room.neighborhoodId)
        assertEquals(100, room.stateId)
        assertEquals("CA", room.country)
        assertEquals(request.pricePerNight, room.pricePerNight)
        assertEquals(request.pricePerMonth, room.pricePerMonth)
        assertEquals(request.currency, room.currency)
        assertEquals(request.checkinTime, room.checkinTime)
        assertEquals(request.checkoutTime, room.checkoutTime)
        assertEquals(request.leaseTerm, room.leaseTerm)
        assertEquals(request.leaseType, room.leaseType)
        assertEquals(request.furnishedType, room.furnishedType)
        assertEquals(request.categoryId, room.categoryId)
    }
}
