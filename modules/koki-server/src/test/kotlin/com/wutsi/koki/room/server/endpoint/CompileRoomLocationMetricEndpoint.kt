package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.server.dao.LocationRepository
import com.wutsi.koki.room.server.dao.RoomLocationMetricRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/CompileRoomLocationMetricEndpoint.sql"])
class CompileRoomLocationMetricEndpoint : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomLocationMetricRepository

    @Autowired
    private lateinit var locationDao: LocationRepository

    @Test
    fun run() {
        rest.getForEntity("/v1/rooms/metrics/locations/compile", Any::class.java)

        Thread.sleep(2000)
        assertCount(2, 2370101L)
        assertCount(3, 2370102L)
        assertCount(1, 237010100L)
        assertCount(3, 237010200L)
        assertCount(1, 237010101L)
    }

    private fun assertCount(expected: Int, locationId: Long) {
        val location = locationDao.findById(locationId).get()
        val stat = dao.findByTenantIdAndLocation(1, location)
        assertEquals(expected, stat?.totalPublishedRentals, "locationId=$locationId")
    }
}
