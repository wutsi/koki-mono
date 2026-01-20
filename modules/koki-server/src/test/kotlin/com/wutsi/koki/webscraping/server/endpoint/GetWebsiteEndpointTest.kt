package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.webscraping.dto.GetWebsiteResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/GetWebsiteEndpoint.sql"])
class GetWebsiteEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        // Given
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        // When
        val response = rest.getForEntity("/v1/websites/100", GetWebsiteResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val website = response.body!!.website
        assertEquals(100L, website.id)
        assertEquals(11L, website.userId)
        assertEquals("https://example.com", website.baseUrl)
        assertEquals("5d41402abc4b2a76b9719d911017c592", website.baseUrlHash)
        assertEquals("https://example.com/listings/", website.listingUrlPrefix)
        assertEquals(listOf("https://example.com?page=1"), website.homeUrls)
        assertEquals(".content", website.contentSelector)
        assertEquals("img.gallery", website.imageSelector)
        assertEquals(true, website.active)
        assertEquals("2020-01-22", fmt.format(website.createdAt))
    }

    @Test
    fun `get website with minimal fields`() {
        // Given
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        // When
        val response = rest.getForEntity("/v1/websites/102", GetWebsiteResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val website = response.body!!.website
        assertEquals(102L, website.id)
        assertEquals(12L, website.userId)
        assertEquals("https://another.com", website.baseUrl)
        assertEquals("https://another.com/items/", website.listingUrlPrefix)
        assertNull(website.contentSelector)
        assertNull(website.imageSelector)
        assertEquals(false, website.active)
        assertEquals(emptyList(), website.homeUrls)
        assertEquals("2020-01-24", fmt.format(website.createdAt))
    }

    @Test
    fun `get website not found`() {
        // When
        val response = rest.getForEntity("/v1/websites/999", ErrorResponse::class.java)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        assertEquals(ErrorCode.WEBSITE_NOT_FOUND, response.body?.error?.code)
    }
}
