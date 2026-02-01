package com.wutsi.koki.webscraping.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/CreateWebpageImagesEndpoint.sql"])
class CreateWebpageImagesEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/100/images", null, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val command = argumentCaptor<CreateFileCommand>()
        verify(publisher, times(2)).publish(command.capture())

        assertEquals("https://picsum.photos/100/300", command.firstValue.url)
        assertEquals(111L, command.firstValue.owner?.id)
        assertEquals(ObjectType.LISTING, command.firstValue.owner?.type)

        assertEquals("https://picsum.photos/100", command.secondValue.url)
        assertEquals(111L, command.secondValue.owner?.id)
        assertEquals(ObjectType.LISTING, command.secondValue.owner?.type)
    }

    @Test
    fun `webpage without listing`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/101/images", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }
}
