package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.SaveRoomGeoLocationRequest
import com.wutsi.koki.room.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/SaveRoomGeoLocationEndpoint.sql"])
class SaveRoomGeoLocationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomRepository

    @Test
    fun geo() {
        val request = SaveRoomGeoLocationRequest(
            longitude = 10.000,
            latitude = 5.00
        )
        val response = rest.postForEntity("/v1/rooms/111/geolocation", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(111).get()

        assertEquals(USER_ID, room.modifiedById)
        assertEquals(request.longitude, room.longitude)
        assertEquals(request.latitude, room.latitude)
    }
}
