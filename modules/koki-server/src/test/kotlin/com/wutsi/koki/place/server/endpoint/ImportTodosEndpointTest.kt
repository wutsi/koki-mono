package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.refdata.server.io.NeighbourhoodImporter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/ImportTodosEndpoint.sql"])
class ImportTodosEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    protected lateinit var neighbourhoodImporter: NeighbourhoodImporter

    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun `import parks successfully`() {
        // GIVEN
        neighbourhoodImporter.import("CM")

        // WHEN
        val response = rest.getForEntity("/v1/places/import/todos?country=CM", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(21, result.added) // 21 entries - 1 existing = 21 new
        assertEquals(1, result.updated) // 1 existing park should be updated
        assertEquals(0, result.errors)
        assertTrue(result.errorMessages.isEmpty())

        val entries = dao.findAll().filter {
            (it.type == PlaceType.PARK || it.type == PlaceType.MUSEUM) && !it.deleted
        }
        val ecoPark = entries.find { it.name == "Eco-Park (Ahala)" }
        assertNotNull(ecoPark)
        assertEquals("eco-park-(ahala)", ecoPark.asciiName)
        assertEquals(PlaceType.PARK, ecoPark.type)
        assertEquals(PlaceStatus.PUBLISHED, ecoPark.status)
        assertTrue(ecoPark.websiteUrl!!.contains("tripadvisor"))
        assertEquals(4.0, ecoPark.rating)
        assertEquals(3.8042, ecoPark.latitude)
        assertEquals(11.4958, ecoPark.longitude)

        val museum = entries.find { it.name == "Blackitude Museum" }

        assertNotNull(museum)
        assertEquals("blackitude-museum", museum.asciiName)
        assertEquals(PlaceType.MUSEUM, museum.type)
        assertEquals(PlaceStatus.PUBLISHED, ecoPark.status)
        assertTrue(museum.websiteUrl!!.contains("tripadvisor"))
    }

    @Test
    fun `invalid country`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/import/todos?country=xxx", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(0, result.added) // 22 entries - 1 existing = 21 new
        assertEquals(0, result.updated) // 1 existing park should be updated
        assertEquals(1, result.errors)
        assertTrue(result.errorMessages.isNotEmpty())
    }
}
