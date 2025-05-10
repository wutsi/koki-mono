package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.CreateRoomUnitResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.server.dao.RoomUnitRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/CreateRoomUnitEndpoint.sql"])
class CreateRoomUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomUnitRepository

    @Test
    fun create() {
        val request = CreateRoomUnitRequest(
            roomId = 111L,
            floor = 1,
            number = "123",
            status = RoomUnitStatus.AVAILABLE,
        )
        val response = rest.postForEntity("/v1/room-units", request, CreateRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val roomUnitId = response.body!!.roomUnitId
        val roomUnit = dao.findById(roomUnitId).get()

        assertEquals(TENANT_ID, roomUnit.tenantId)
        assertEquals(USER_ID, roomUnit.createdById)
        assertEquals(USER_ID, roomUnit.modifiedById)
        assertEquals(request.floor, roomUnit.floor)
        assertEquals(request.number, roomUnit.number)
        assertEquals(request.status, roomUnit.status)
    }

    @Test
    fun `duplicate number`() {
        val request = CreateRoomUnitRequest(
            roomId = 111L,
            floor = 33,
            number = "333",
            status = RoomUnitStatus.AVAILABLE,
        )
        val response = rest.postForEntity("/v1/room-units", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_UNIT_DUPLICATE_NUMBER, response.body?.error?.code)
    }
}
