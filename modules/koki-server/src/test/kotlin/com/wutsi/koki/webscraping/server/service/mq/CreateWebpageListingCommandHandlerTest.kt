package com.wutsi.koki.webscraping.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CreateWebpageListingCommandHandlerTest {
    private val service = mock<WebpageService>()
    private val publisher = mock<Publisher>()
    private val logger = DefaultKVLogger()
    private val handler = CreateWebpageListingCommandHandler(service, publisher, logger)

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
        doReturn(webpage).whenever(service).listing(any(), any())

        // WHEN
        val cmd = CreateWebpageListingCommand(webpage.id!!, webpage.tenantId)
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
    fun `ignore error when listing already created`() {
        // GIVEN
        val ex = ConflictException(
            error = Error(
                code = ErrorCode.LISTING_ALREADY_CREATED,
            )
        )
        doThrow(ex).whenever(service).listing(any(), any())

        // WHEN
        val cmd = CreateWebpageListingCommand(111L, 333L)
        val result = handler.handle(cmd)

        // THEN
        assertFalse(result)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `ignore error when city not found`() {
        // GIVEN
        val ex = NotFoundException(
            error = Error(
                code = ErrorCode.LOCATION_NOT_FOUND,
            )
        )
        doThrow(ex).whenever(service).listing(any(), any())

        // WHEN
        val cmd = CreateWebpageListingCommand(111L, 333L)
        val result = handler.handle(cmd)

        // THEN
        assertFalse(result)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun noContent() {
        // GIVEN
        val ex = ConflictException(
            error = Error(
                code = ErrorCode.WEBPAGE_NO_CONTENT,
            )
        )
        doThrow(ex).whenever(service).listing(any(), any())

        // WHEN
        val cmd = CreateWebpageListingCommand(111L, 333L)
        val result = assertThrows<ConflictException> { handler.handle(cmd) }

        // THEN
        assertEquals(ex.error, result.error)
        verify(publisher, never()).publish(any())
    }

    @Test
    fun error() {
        // GIVEN
        val ex = IllegalStateException("Failed")
        doThrow(ex).whenever(service).listing(any(), any())

        // WHEN
        val cmd = CreateWebpageListingCommand(111L, 333L)
        val result = assertThrows<IllegalStateException> { handler.handle(cmd) }

        // THEN
        assertEquals(ex, result)
        verify(publisher, never()).publish(any())
    }
}
