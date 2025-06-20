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
        val response = rest.getForEntity("/v1/rooms?type=HOUSE", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(4, rooms.size)
        assertEquals(listOf(111L, 112L, 113L, 115L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/rooms?status=PUBLISHING", SearchRoomResponse::class.java)

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

    @Test
    fun `by rooms`() {
        val response = rest.getForEntity("/v1/rooms?min-rooms=2&max-rooms=3", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(4, rooms.size)
        assertEquals(listOf(111L, 112L, 113L, 114L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by bathrooms`() {
        val response = rest.getForEntity("/v1/rooms?min-bathrooms=5&max-bathrooms=6", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(115L, 116L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by neighborhood`() {
        val response = rest.getForEntity("/v1/rooms?neighborhood-id=100112", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(112L, 114L), rooms.map { room -> room.id }.sorted())
    }

    @Test
    fun `by category-id`() {
        val response = rest.getForEntity("/v1/rooms?category-id=55&category-id=33", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(3, rooms.size)
        assertEquals(listOf(111L, 115L, 116L), rooms.map { it.id })
    }

    @Test
    fun `by amenity-id`() {
        val response = rest.getForEntity("/v1/rooms?amenity-id=1&amenity-id=2", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(3, rooms.size)
        assertEquals(listOf(111L, 114L, 115L), rooms.map { it.id })
    }

    @Test
    fun `by manager-id`() {
        val response = rest.getForEntity("/v1/rooms?account-manager-id=11", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(2, rooms.size)
        assertEquals(listOf(112L, 114L), rooms.map { it.id })
    }

    @Test
    fun `by account-id`() {
        val response = rest.getForEntity("/v1/rooms?account-id=31&account-id=32", SearchRoomResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.rooms
        assertEquals(3, rooms.size)
        assertEquals(listOf(112L, 114L, 116L), rooms.map { it.id })
    }
}
