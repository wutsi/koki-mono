package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.webscraping.dto.UpdateWebsiteRequest
import com.wutsi.koki.webscraping.server.dao.WebsiteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/UpdateWebsiteEndpoint.sql"])
class UpdateWebsiteEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: WebsiteRepository

    @Test
    fun update() {
        // When
        val request = UpdateWebsiteRequest(
            listingUrlPrefix = "/properties/",
            homeUrls = listOf("https://www.example.com/properties/page-1", "https://www.example.com/properties/page-2"),
            contentSelector = ".description",
            imageSelector = "img.photo",
            active = true,

            )
        val response = rest.postForEntity(
            "/v1/websites/100",
            request,
            Any::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)

        val website = dao.findById(100).get()
        assertEquals(request.listingUrlPrefix, website.listingUrlPrefix)
        assertEquals(request.contentSelector, website.contentSelector)
        assertEquals(request.imageSelector, website.imageSelector)
        assertEquals(request.active, website.active)
        assertEquals(request.homeUrls, website.homeUrls)
    }

    @Test
    fun `update with minimal fields`() {
        // When
        val request = UpdateWebsiteRequest(
            listingUrlPrefix = "https://example.com/homes/",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites/100",
            request,
            Any::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)

        val website = dao.findById(100).get()
        assertEquals(request.listingUrlPrefix, website.listingUrlPrefix)
        assertEquals(null, website.contentSelector)
        assertEquals(null, website.imageSelector)
        assertEquals(true, website.active)
        assertEquals(emptyList(), website.homeUrls)
    }

    @Test
    fun `update non-existent website`() {
        // When
        val request = UpdateWebsiteRequest(
            listingUrlPrefix = "https://example.com/properties/",
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites/999",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.WEBSITE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `update without listing url prefix`() {
        // When
        val request = UpdateWebsiteRequest(
            listingUrlPrefix = "", // Empty prefix
            active = true
        )
        val response = rest.postForEntity(
            "/v1/websites/100",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}
