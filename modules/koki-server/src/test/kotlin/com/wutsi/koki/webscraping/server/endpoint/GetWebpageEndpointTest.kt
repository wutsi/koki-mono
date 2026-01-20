package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.webscraping.dto.GetWebpageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/GetWebpageEndpoint.sql"])
class GetWebpageEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages/200",
            GetWebpageResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val webpage = response.body!!.webpage
        assertEquals(200L, webpage.id)
        assertEquals(100L, webpage.websiteId)
        assertEquals("https://example.com/listings/1", webpage.url)
        assertEquals("Listing 1 content with detailed description", webpage.content)
        assertEquals(2, webpage.imageUrls.size)
        assertEquals("https://example.com/img1.jpg", webpage.imageUrls[0])
        assertEquals("https://example.com/img2.jpg", webpage.imageUrls[1])
        assertEquals(true, webpage.active)
        assertNotNull(webpage.createdAt)
    }

    @Test
    fun `get non-existent webpage`() {
        // When
        val response = rest.getForEntity(
            "/v1/webpages/999",
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.WEBPAGE_NOT_FOUND, response.body?.error?.code)
    }
}
