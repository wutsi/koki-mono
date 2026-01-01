package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.server.dao.PlaceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/ImportSchoolsEndpoint.sql"])
class ImportSchoolsEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun `import schools successfully`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(70, result.added) // 38 schools - 1 existing = 37 new
        assertEquals(1, result.updated) // 1 existing school should be updated
        assertEquals(0, result.errors)
        assertTrue(result.errorMessages.isEmpty())
    }
}
