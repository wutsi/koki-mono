package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.place.server.dao.PlaceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/DeletePlaceEndpoint.sql"])
class DeletePlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun delete() {
        // WHEN
        rest.delete("/v1/places/100")

        // THEN
        val place = dao.findById(100L).get()
        assertNotNull(place.deletedAt)
        assertTrue(place.deleted)
        assertEquals(USER_ID, place.modifiedById)
        assertNotNull(place.modifiedAt)
    }

    @Test
    fun `delete not found`() {
        // WHEN
        val response = rest.exchange(
            "/v1/places/999",
            org.springframework.http.HttpMethod.DELETE,
            null,
            ErrorResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PLACE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `delete from different tenant`() {
        // WHEN - Try to delete place from tenant 2
        val response = rest.exchange(
            "/v1/places/200",
            org.springframework.http.HttpMethod.DELETE,
            null,
            ErrorResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PLACE_NOT_FOUND, response.body?.error?.code)

        // Verify place from tenant 2 is not deleted
        val place = dao.findById(200L).get()
        assertEquals(null, place.deletedAt)
    }

    @Test
    fun `delete is idempotent`() {
        // WHEN - Delete twice
        rest.delete("/v1/places/100")
        rest.delete("/v1/places/100")

        // THEN - No error, place is deleted
        val place = dao.findById(100L).get()
        assertTrue(place.deleted)
        assertNotNull(place.deletedAt)
    }
}
