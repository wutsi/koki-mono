package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.room.dto.SearchRoomLocationMetricResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/SearchRoomLocationMetricEndpoint.sql"])
class SearchRoomLocationMetricEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/rooms/metrics/locations", SearchRoomLocationMetricResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.metrics
        assertEquals(6, rooms.size)
    }

    @Test
    fun `by country`() {
        val response =
            rest.getForEntity("/v1/rooms/metrics/locations?country=CA", SearchRoomLocationMetricResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.metrics
        assertEquals(1, rooms.size)
    }

    @Test
    fun `by location-type`() {
        val response =
            rest.getForEntity(
                "/v1/rooms/metrics/locations?location-type=CITY",
                SearchRoomLocationMetricResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.metrics
        assertEquals(4, rooms.size)
    }

    @Test
    fun `by parent-type-id and type`() {
        val response = rest.getForEntity(
            "/v1/rooms/metrics/locations?parent-location-id=23701&location-type=CITY",
            SearchRoomLocationMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val rooms = response.body!!.metrics
        assertEquals(2, rooms.size)
    }
}
