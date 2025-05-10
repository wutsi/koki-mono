package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.SearchRoomUnitResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/SearchRoomUnitEndpoint.sql"])
class SearchRoomUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/room-units", SearchRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.roomUnits
        assertEquals(6, rooms.size)
    }

    @Test
    fun `by room`() {
        val response = rest.getForEntity("/v1/room-units?room-id=112", SearchRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.roomUnits
        assertEquals(2, rooms.size)
        assertEquals(listOf(1120L, 1121), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/room-units?status=AVAILABLE", SearchRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.roomUnits
        assertEquals(3, rooms.size)
        assertEquals(listOf(1111L, 1113, 1121), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/room-units?id=1111&id=1121&id=2000", SearchRoomUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.roomUnits
        assertEquals(2, rooms.size)
        assertEquals(listOf(1111L, 1121L), rooms.map { room -> room.id }.sorted())
    }
}
