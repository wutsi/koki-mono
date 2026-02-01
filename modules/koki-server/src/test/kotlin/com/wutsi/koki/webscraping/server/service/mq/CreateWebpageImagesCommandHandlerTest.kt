package com.wutsi.koki.webscraping.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageImagesCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class CreateWebpageImagesCommandHandlerTest {
    private val service = mock<WebpageService>()
    private val publisher = mock<Publisher>()
    private val logger = DefaultKVLogger()
    private val handler = CreateWebpageImagesCommandHandler(
        publisher = publisher,
        service = service,
        logger = logger,
    )

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun handle() {
        // GIVEN
        val webpage = WebpageEntity(
            id = 111L,
            tenantId = 333L,
            listingId = 444L,
            imageUrls = listOf("url1", "url2")
        )
        doReturn(webpage).whenever(service).get(any(), any())

        // WHEN
        val cmd = CreateWebpageImagesCommand(webpage.id!!, webpage.tenantId)
        val result = handler.handle(cmd)

        // THEN
        assertTrue(result)

        val command = argumentCaptor<CreateFileCommand>()
        verify(publisher, times(2)).publish(command.capture())

        assertEquals(webpage.imageUrls[0], command.firstValue.url)
        assertEquals(webpage.listingId, command.firstValue.owner?.id)
        assertEquals(ObjectType.LISTING, command.firstValue.owner?.type)

        assertEquals(webpage.imageUrls[1], command.secondValue.url)
        assertEquals(webpage.listingId, command.secondValue.owner?.id)
        assertEquals(ObjectType.LISTING, command.secondValue.owner?.type)
    }

    @Test
    fun `when webpage has no listing - throw LISTING_NOT_FOUND`() {
        // GIVEN
        val webpage = WebpageEntity(
            id = 111L,
            tenantId = 333L,
            listingId = null,
            imageUrls = listOf("url1", "url2")
        )
        doReturn(webpage).whenever(service).get(any(), any())

        // WHEN
        val cmd = CreateWebpageImagesCommand(webpage.id!!, webpage.tenantId)
        val ex = assertThrows<NotFoundException> { handler.handle(cmd) }

        // THEN
        assertEquals(ErrorCode.LISTING_NOT_FOUND, ex.error.code)
        verify(publisher, never()).publish(any())
    }
}
