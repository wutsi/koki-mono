package com.wutsi.koki.lodging.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.lodging.dto.GetRoomResponse
import com.wutsi.koki.lodging.dto.RoomStatus
import com.wutsi.koki.lodging.dto.RoomType
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lodging/GetRoomEndpoint.sql"])
class GetRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/rooms/111", GetRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = response.body!!.room
        assertEquals(RoomStatus.UNDER_REVIEW, room.status)
        assertEquals(RoomType.PROPERTY, room.type)
        assertEquals("Room A", room.title)
        assertEquals("This is the title of the room", room.description)
        assertEquals(10, room.maxGuests)
        assertEquals(6, room.numberOfRooms)
        assertEquals(2, room.numberOfBathrooms)
        assertEquals(4, room.numberOfBeds)
        assertEquals("3030 Linton", room.address.street)
        assertEquals("11111", room.address.postalCode)
        assertEquals(1001L, room.address.cityId)
        assertEquals(100, room.address.stateId)
        assertEquals("CA", room.address.country)
        assertEquals(35.0, room.pricePerNight.amount)
        assertEquals("CAD", room.pricePerNight.currency)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/rooms/112", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad id`() {
        val response = rest.getForEntity("/v1/rooms/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad tenant`() {
        val response = rest.getForEntity("/v1/rooms/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_NOT_FOUND, response.body?.error?.code)
    }
}
