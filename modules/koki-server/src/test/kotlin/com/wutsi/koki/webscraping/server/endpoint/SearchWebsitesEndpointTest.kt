package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.webscraping.dto.SearchWebsiteResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/SearchWebsitesEndpoint.sql"])
class SearchWebsitesEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `search all websites`() {
        // When
        val response = rest.getForEntity("/v1/websites", SearchWebsiteResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(5, websites.size)
    }

    @Test
    fun `search websites by ids`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?id=100&id=102",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
        assertEquals(100L, websites[0].id)
        assertEquals(102L, websites[1].id)
    }

    @Test
    fun `search websites by user ids`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?user-id=11",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
        assertEquals(11L, websites[0].userId)
        assertEquals(11L, websites[1].userId)
    }

    @Test
    fun `search websites by multiple user ids`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?user-id=11&user-id=13",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(3, websites.size)
    }

    @Test
    fun `search active websites`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?active=true",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(3, websites.size)
        websites.forEach { website ->
            assertEquals(true, website.active)
        }
    }

    @Test
    fun `search inactive websites`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?active=false",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
        websites.forEach { website ->
            assertEquals(false, website.active)
        }
    }

    @Test
    fun `search websites with limit`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?limit=2",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
    }

    @Test
    fun `search websites with offset`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?offset=3",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
    }

    @Test
    fun `search websites with limit and offset`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?limit=2&offset=1",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(2, websites.size)
    }

    @Test
    fun `search websites with no results`() {
        // When
        val response = rest.getForEntity(
            "/v1/websites?id=999",
            SearchWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val websites = response.body!!.websites
        assertEquals(0, websites.size)
    }
}
