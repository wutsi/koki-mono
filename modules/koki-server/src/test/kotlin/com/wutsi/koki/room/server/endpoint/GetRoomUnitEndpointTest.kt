package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.dto.GetRoomUnitResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/GetRoomUnitEndpoint.sql"])
class GetRoomUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/room-units/1110", GetRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val roomUnit = response.body!!.roomUnit
        assertEquals(RoomUnitStatus.UNDER_MAINTENANCE, roomUnit.status)
        assertEquals(1, roomUnit.floor)
        assertEquals("123", roomUnit.number)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/room-units/1111", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_UNIT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad id`() {
        val response = rest.getForEntity("/v1/room-units/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_UNIT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad tenant`() {
        val response = rest.getForEntity("/v1/room-units/2000", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROOM_UNIT_NOT_FOUND, response.body?.error?.code)
    }
}
