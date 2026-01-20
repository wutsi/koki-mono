package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.webscraping.dto.CreateWebsiteRequest
import com.wutsi.koki.webscraping.dto.CreateWebsiteResponse
import com.wutsi.koki.webscraping.server.dao.WebsiteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/CreateWebsiteEndpoint.sql"])
class CreateWebsiteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: WebsiteRepository

    @Test
    fun create() {
        // When
        val request = CreateWebsiteRequest(
            userId = USER_ID,
            baseUrl = "https://newsite.com",
            listingUrlPrefix = "https://newsite.com/listings/",
            homeUrls = listOf("https://newsite.com?page=1", "https://newsite.com?page=2"),
            contentSelector = ".content",
            imageSelector = "img.gallery",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites",
            request,
            CreateWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val websiteId = response.body!!.websiteId
        assert(websiteId > 0)

        val website = dao.findById(websiteId).get()
        assertEquals(request.userId, website.userId)
        assertEquals(request.baseUrl, website.baseUrl)
        assertEquals(request.listingUrlPrefix, website.listingUrlPrefix)
        assertEquals(request.contentSelector, website.contentSelector)
        assertEquals(request.imageSelector, website.imageSelector)
        assertEquals(request.active, website.active)
        assertEquals(request.homeUrls, website.homeUrls)
    }

    @Test
    fun `create with minimal fields`() {
        // When
        val request = CreateWebsiteRequest(
            userId = USER_ID,
            baseUrl = "https://minimal.com",
            listingUrlPrefix = "https://minimal.com/listings/",
        )
        val response = rest.postForEntity(
            "/v1/websites",
            request,
            CreateWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val websiteId = response.body!!.websiteId
        assert(websiteId > 0)

        val website = dao.findById(websiteId).get()
        assertEquals(request.userId, website.userId)
        assertEquals(request.baseUrl, website.baseUrl)
        assertEquals(request.listingUrlPrefix, website.listingUrlPrefix)
        assertEquals(null, website.contentSelector)
        assertEquals(null, website.imageSelector)
        assertEquals(true, website.active)
        assertEquals(emptyList(), website.homeUrls)
    }

    @Test
    fun `create duplicate base url`() {
        // When
        val request = CreateWebsiteRequest(
            userId = USER_ID,
            baseUrl = "https://example.com",
            listingUrlPrefix = "https://example.com/listings/",
            contentSelector = ".content",
            imageSelector = "img.gallery",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.WEBSITE_DUPLICATE_BASE_URL, response.body?.error?.code)
    }

    @Test
    fun `create without base url`() {
        // When
        val request = CreateWebsiteRequest(
            userId = USER_ID,
            baseUrl = "",
            listingUrlPrefix = "https://newsite.com/listings/",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `create without listing url prefix`() {
        // When
        val request = CreateWebsiteRequest(
            userId = USER_ID,
            baseUrl = "https://newsite.com",
            listingUrlPrefix = "",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}
