package com.wutsi.koki.webscraping.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteResponse
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebscraperService
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/ScrapeWebsiteEndpoint.sql"])
class ScrapeWebsiteEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var webscraper: WebscraperService

    @Test
    fun scrape() {
        // GIVEN
        doReturn(
            listOf(
                WebpageEntity(id = 1),
                WebpageEntity(id = 2),
                WebpageEntity(id = 3)
            )
        ).whenever(webscraper).scrape(any(), any())

        // When
        val request = ScrapeWebsiteRequest()
        val response = rest.postForEntity(
            "/v1/websites/100/scrape",
            request,
            ScrapeWebsiteResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(3, response.body!!.webpageImported)
    }

    @Test
    fun `scrape non-existent website`() {
        // When
        val request = ScrapeWebsiteRequest()
        val response = rest.postForEntity(
            "/v1/websites/999/scrape",
            request,
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.WEBSITE_NOT_FOUND, response.body?.error?.code)
    }
}
