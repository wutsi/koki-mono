package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.webscraping.dto.SearchWebpageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/SearchWebpagesEndpoint.sql"])
class SearchWebpagesEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `search all`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(4, response.body!!.webpages.size)
    }

    @Test
    fun `search by website id`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?website-id=100",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val webpages = response.body!!.webpages
        assertEquals(3, webpages.size)
        assert(webpages.all { it.websiteId == 100L })
    }

    @Test
    fun `search by listing id`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?listing-id=1201",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val webpages = response.body!!.webpages
        assertEquals(1, webpages.size)
        assert(webpages.all { it.listingId == 1201L })
    }

    @Test
    fun `search by active status`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?active=true",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val webpages = response.body!!.webpages
        assertEquals(3, webpages.size)
        assert(webpages.all { it.active })
    }

    @Test
    fun `search by website id and active status`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?website-id=100&active=true",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val webpages = response.body!!.webpages
        assertEquals(2, webpages.size)
        assert(webpages.all { it.websiteId == 100L && it.active })
    }

    @Test
    fun `search with limit`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?limit=2",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.webpages.size)
    }

    @Test
    fun `search with offset`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?limit=2&offset=2",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.webpages.size)
    }

    @Test
    fun `search inactive webpages`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages?active=false",
            SearchWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val webpages = response.body!!.webpages
        assertEquals(1, webpages.size)
        assertEquals(false, webpages[0].active)
    }
}
