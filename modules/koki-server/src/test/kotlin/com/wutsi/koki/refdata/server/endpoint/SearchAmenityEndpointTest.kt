package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.dto.SearchAmenityResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/SearchAmenityEndpoint.sql"])
class SearchAmenityEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/amenities", SearchAmenityResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val amenities = response.body!!.amenities
        assertEquals(5, amenities.size)
    }

    @Test
    fun `by active`() {
        val response = rest.getForEntity("/v1/amenities?active=false", SearchAmenityResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.amenities
        assertEquals(2, categories.size)
        assertEquals(listOf(1103L, 1201L), categories.map { it.id })
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/amenities?id=1101&id=1102&id=9999", SearchAmenityResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.amenities
        assertEquals(2, categories.size)
        assertEquals(listOf(1101L, 1102L), categories.map { it.id })
    }

    @Test
    fun `by category`() {
        val response = rest.getForEntity("/v1/amenities?category-id=1100", SearchAmenityResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.amenities
        assertEquals(3, categories.size)
        assertEquals(listOf(1101L, 1102L, 1103L), categories.map { it.id })
    }
}
