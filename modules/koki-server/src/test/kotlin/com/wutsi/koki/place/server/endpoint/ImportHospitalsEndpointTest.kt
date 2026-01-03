package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.server.io.NeighbourhoodImporter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/ImportHospitalsEndpoint.sql"])
class ImportHospitalsEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    protected lateinit var neighbourhoodImporter: NeighbourhoodImporter

    @Test
    fun `import hospitals successfully`() {
        // GIVEN
        neighbourhoodImporter.import("CM")

        // WHEN
        val response = rest.getForEntity("/v1/places/import/hospitals?country=CM", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(42, result.added) // 43 hospitals - 1 existing = 43 new
        assertEquals(1, result.updated) // 1 existing hospital should be updated
        assertEquals(0, result.errors)
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun `invalid country`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/import/hospitals?country=xxx", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(0, result.added) // 22 entries - 1 existing = 21 new
        assertEquals(0, result.updated) // 1 existing park should be updated
        assertEquals(1, result.errors)
        assertTrue(result.errorMessages.isNotEmpty())
    }
}
