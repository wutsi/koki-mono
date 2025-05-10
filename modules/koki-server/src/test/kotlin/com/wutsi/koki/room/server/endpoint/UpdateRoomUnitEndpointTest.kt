package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import com.wutsi.koki.room.server.dao.RoomUnitRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/UpdateRoomUnitEndpoint.sql"])
class UpdateRoomUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomUnitRepository

    @Test
    fun update() {
        val request = UpdateRoomUnitRequest(
            floor = 3,
            number = "333",
            status = RoomUnitStatus.UNDER_MAINTENANCE,
        )
        val response = rest.postForEntity("/v1/room-units/1110", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val roomUnit = dao.findById(1110L).get()

        assertEquals(TENANT_ID, roomUnit.tenantId)
        assertEquals(USER_ID, roomUnit.modifiedById)
        assertEquals(request.floor, roomUnit.floor)
        assertEquals(request.number, roomUnit.number)
        assertEquals(request.status, roomUnit.status)
    }

    @Test
    fun `duplicate number`() {
        val request = UpdateRoomUnitRequest(
            floor = 3,
            number = "555",
            status = RoomUnitStatus.UNDER_MAINTENANCE,
        )
        val response = rest.postForEntity("/v1/room-units/1110", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_UNIT_DUPLICATE_NUMBER, response.body?.error?.code)
    }
}
