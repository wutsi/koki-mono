package com.wutsi.koki.webscraping.server.endpoint

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.webscraping.dto.SearchWebpagesResponse
import com.wutsi.koki.webscraping.dto.WebpageSummary
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateWebsiteListingsEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var webpageEndpoint: WebpageEndpoints

    val webpages = listOf(
        WebpageSummary(id = 11L),
        WebpageSummary(id = 12L),
        WebpageSummary(id = 13L)
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchWebpagesResponse(webpages)).whenever(webpageEndpoint).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity("/v1/websites/1/listings", null, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(1000L)
        val websiteId = argumentCaptor<Long>()
        verify(webpageEndpoint, times(webpages.size)).listing(eq(TENANT_ID), websiteId.capture())

        assertEquals(webpages.map { it.id }, websiteId.allValues)
    }

    @Test
    fun `ignore errors`() {
        // GIVEN
        doThrow(IllegalStateException::class).whenever(webpageEndpoint).listing(
            eq(TENANT_ID),
            eq(webpages[1].id)
        )

        // WHEN
        val response = rest.postForEntity("/v1/websites/1/listings", null, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(1000L)
        val websiteId = argumentCaptor<Long>()
        verify(webpageEndpoint, times(webpages.size)).listing(eq(TENANT_ID), websiteId.capture())

        assertEquals(webpages.map { it.id }, websiteId.allValues)
    }
}
