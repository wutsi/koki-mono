package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/DeleteRoomEndpoint.sql"])
class DeleteRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomRepository

    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    @Test
    fun delete() {
        rest.delete("/v1/rooms/111")

        val room = dao.findById(111).get()
        assertEquals(USER_ID, room.deleteById)
        assertEquals(fmt.format(Date()), fmt.format(room.deletedAt))
        assertEquals(true, room.deleted)
    }

    @Test
    fun `already deleted`() {
        rest.delete("/v1/rooms/112")

        val room = dao.findById(112).get()
        assertEquals(3333L, room.deleteById)
        assertEquals("2020-01-10", fmt.format(room.deletedAt))
        assertEquals(true, room.deleted)
    }
}
