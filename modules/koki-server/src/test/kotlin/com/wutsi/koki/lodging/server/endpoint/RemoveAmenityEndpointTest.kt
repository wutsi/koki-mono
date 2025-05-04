package com.wutsi.koki.lodging.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.lodging.dto.AddAmenityRequest
import com.wutsi.koki.lodging.dto.CreateRoomRequest
import com.wutsi.koki.lodging.dto.CreateRoomResponse
import com.wutsi.koki.lodging.dto.RoomStatus
import com.wutsi.koki.lodging.dto.RoomType
import com.wutsi.koki.lodging.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lodging/AddAmenityEndpoint.sql"])
class AddAmenityEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var ds: DataSource

    private fun getAmenityIds(roomId: Long): List<Long> {
        val result = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT amenity_fk FROM T_ROOM_AMENITY where room_fk=$roomId")
                rs.use {
                    while (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result.sorted()
    }

    @Test
    fun add() {
        val request = AddAmenityRequest(
            amenityId = 100
        )
        val response = rest.postForEntity("/v1/rooms/111/amenities", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val amenityIds = getAmenityIds(111L)
        assertEquals(5, amenityIds.size)
        assertEquals(listOf(1L, 2L, 3L, 4L, 100L), amenityIds)
    }

    @Test
    fun `add again`() {
        val request = AddAmenityRequest(
            amenityId = 1
        )
        val response = rest.postForEntity("/v1/rooms/112/amenities", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val amenityIds = getAmenityIds(112L)
        assertEquals(2, amenityIds.size)
        assertEquals(listOf(1L, 2L), amenityIds)
    }
}
