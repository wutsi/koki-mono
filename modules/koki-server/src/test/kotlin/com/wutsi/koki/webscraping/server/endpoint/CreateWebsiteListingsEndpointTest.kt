package com.wutsi.koki.webscraping.server.endpoint

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateWebsiteListingsEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @MockitoBean
    private lateinit var webpageService: WebpageService

    val webpages = listOf(
        WebpageEntity(id = 11L, tenantId = TENANT_ID, listingId = null),
        WebpageEntity(id = 12L, tenantId = TENANT_ID, listingId = null),
        WebpageEntity(id = 13L, tenantId = TENANT_ID, listingId = 333L),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(webpages).whenever(webpageService).search(
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
        val command = argumentCaptor<CreateWebpageListingCommand>()
        verify(publisher, times(2)).publish(command.capture())

        assertEquals(webpages[0].id, command.firstValue.webpageId)
        assertEquals(webpages[0].tenantId, command.firstValue.tenantId)

        assertEquals(webpages[1].id, command.secondValue.webpageId)
        assertEquals(webpages[1].tenantId, command.secondValue.tenantId)
    }
}
