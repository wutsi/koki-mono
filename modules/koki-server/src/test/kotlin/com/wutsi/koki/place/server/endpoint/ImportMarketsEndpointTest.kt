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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/ImportMarketsEndpoint.sql"])
class ImportMarketsEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    protected lateinit var neighbourhoodImporter: NeighbourhoodImporter

    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun `import markets successfully`() {
        // GIVEN
        neighbourhoodImporter.import("CM")

        // WHEN
        val response = rest.getForEntity("/v1/places/import/markets?country=CM", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(52, result.added) // 53 markets - 1 existing = 30 new
        assertEquals(1, result.updated) // 1 existing market should be updated
        assertEquals(0, result.errors)
        assertTrue(result.errorMessages.isEmpty())

        val markets = dao.findAll().filter {
            (it.type == PlaceType.MARKET || it.type == PlaceType.SUPERMARKET) && !it.deleted
        }
        val dovv = markets.find { it.name == "DOVV Bastos" }
        assertNotNull(dovv)
        assertEquals("dovv-bastos", dovv.asciiName)
        assertEquals(PlaceType.SUPERMARKET, dovv.type)
        assertEquals(PlaceStatus.PUBLISHED, dovv.status)
        assertEquals(false, dovv.international)
        assertEquals("https://dovv-distribution.com/", dovv.websiteUrl)

        val marcheCentral = markets.find { it.name == "Marché Central" }

        assertNotNull(marcheCentral)
        assertEquals(PlaceType.MARKET, marcheCentral.type) // Traditional MARKET
        assertEquals(PlaceStatus.PUBLISHED, marcheCentral.status)
        assertEquals(false, marcheCentral.international)
        assertNull(marcheCentral.websiteUrl) // No website
    }

    @Test
    fun `invalid country`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/import/markets?country=xxx", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(0, result.added) // 22 entries - 1 existing = 21 new
        assertEquals(0, result.updated) // 1 existing park should be updated
        assertEquals(1, result.errors)
        assertTrue(result.errorMessages.isNotEmpty())
    }
}
