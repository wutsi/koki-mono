package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.dto.SearchCategoryResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/SearchCategoryEndpoint.sql"])
class SearchCategoryEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/categories", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(9, categories.size)
    }

    @Test
    fun `by level`() {
        val response = rest.getForEntity("/v1/categories?level=0", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(2, categories.size)
        assertEquals(listOf(1100L, 2100L), categories.map { it.id })
    }

    @Test
    fun `by parent`() {
        val response = rest.getForEntity("/v1/categories?parent-id=1100", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(3, categories.size)
        assertEquals(listOf(1110L, 1120L, 1130L), categories.map { it.id })
    }

    @Test
    fun `by type`() {
        val response = rest.getForEntity("/v1/categories?type=SERVICE", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(6, categories.size)
        assertEquals(listOf(1100L, 1110L, 1120L, 1130L, 1131L, 1132L), categories.map { it.id })
    }

    @Test
    fun `by active`() {
        val response = rest.getForEntity("/v1/categories?active=false", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(2, categories.size)
        assertEquals(listOf(2110L, 2120L), categories.map { it.id })
    }

    @Test
    fun `by keyword`() {
        val response = rest.getForEntity("/v1/categories?q=b", SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(5, categories.size)
        assertEquals(listOf(1110L, 1120L, 1130L), categories.map { it.id })
    }
}
