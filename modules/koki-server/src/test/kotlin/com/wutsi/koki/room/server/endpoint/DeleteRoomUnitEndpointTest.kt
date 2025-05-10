package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.server.dao.RoomUnitRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/DeleteRoomUnitEndpoint.sql"])
class DeleteRoomUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomUnitRepository

    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    @Test
    fun delete() {
        rest.delete("/v1/room-units/1110")

        val roomUnit = dao.findById(1110).get()
        assertEquals(USER_ID, roomUnit.deleteById)
        assertEquals(fmt.format(Date()), fmt.format(roomUnit.deletedAt))
        assertEquals(true, roomUnit.deleted)
    }

    @Test
    fun `already deleted`() {
        rest.delete("/v1/room-units/1111")

        val room = dao.findById(1111).get()
        assertEquals(3333L, room.deleteById)
        assertEquals("2020-02-01", fmt.format(room.deletedAt))
        assertEquals(true, room.deleted)
    }
}
