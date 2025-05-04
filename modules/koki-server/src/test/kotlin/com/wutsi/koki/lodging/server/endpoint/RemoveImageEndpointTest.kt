package com.wutsi.koki.lodging.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lodging/RemoveAmenityEndpoint.sql"])
class RemoveAmenityEndpointTest : AuthorizationAwareEndpointTest() {
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
    fun delete() {
        rest.delete("/v1/rooms/111/amenities/1")

        val amenityIds = getAmenityIds(111L)
        assertEquals(3, amenityIds.size)
        assertEquals(listOf(2L, 3L, 4L), amenityIds)
    }

    @Test
    fun `delete invalid amenity`() {
        rest.delete("/v1/rooms/112/amenities/100")

        val amenityIds = getAmenityIds(112L)
        assertEquals(2, amenityIds.size)
        assertEquals(listOf(1L, 2L), amenityIds)
    }
}
