package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.SearchRoomResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/SearchRoomEndpoint.sql"])
class SearchRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/rooms", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(6, rooms.size)
    }

    @Test
    fun `by type`() {
        val response = rest.getForEntity("/v1/rooms?type=PROPERTY", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(4, rooms.size)
        assertEquals(listOf(111L, 112L, 113L, 115L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/rooms?status=UNDER_REVIEW", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(111L, 113L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by city`() {
        val response = rest.getForEntity("/v1/rooms?city-id=2001", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(115L, 116L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `total guest`() {
        val response = rest.getForEntity("/v1/rooms?total-guests=3", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(3, rooms.size)
        assertEquals(listOf(113L, 114L, 115L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/rooms?id=111&id=113&id=200", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(111L, 113L), rooms.map { room -> room.id }.sorted())
    }
}
