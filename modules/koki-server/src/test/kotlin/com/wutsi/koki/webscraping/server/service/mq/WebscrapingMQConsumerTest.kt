package com.wutsi.koki.webscraping.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

class WebscrapingMQConsumerTest {
    private val createWebpageListingCommandHandler = mock<CreateWebpageListingCommandHandler>()
    private val logger: KVLogger = DefaultKVLogger()
    private val consumer = WebscrapingMQConsumer(
        createWebpageListingCommandHandler = createWebpageListingCommandHandler,
        logger = logger
    )

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `createWebpageListing successful`() {
        // GIVEN
        doReturn(WebpageEntity()).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val cmd = CreateWebpageListingCommand()
        val result = consumer.consume(cmd)

        // THEN
        assertTrue(result)
        verify(createWebpageListingCommandHandler).handle(cmd)
    }

    @Test
    fun `createWebpageListing - when listing already created, ignore error`() {
        // GIVEN
        val ex = ConflictException(
            error = Error(
                code = ErrorCode.LISTING_ALREADY_CREATED,
            )
        )
        doThrow(ex).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val result = consumer.consume(CreateWebpageListingCommand())

        // THEN
        assertFalse(result)
    }

    @Test
    fun `createWebpageListing - when city not found, ignore error`() {
        // GIVEN
        val ex = NotFoundException(
            error = Error(
                code = ErrorCode.LOCATION_NOT_FOUND,
            )
        )
        doThrow(ex).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val result = consumer.consume(CreateWebpageListingCommand())

        // THEN
        assertFalse(result)
    }

    @Test
    fun `createWebpageListing - when webpage not found, ignore error`() {
        // GIVEN
        val ex = NotFoundException(
            error = Error(
                code = ErrorCode.WEBPAGE_NOT_FOUND,
            )
        )
        doThrow(ex).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val result = consumer.consume(CreateWebpageListingCommand())

        // THEN
        assertFalse(result)
    }

    @Test
    fun `createWebpageListing - on WutsiException, rethrow error`() {
        // GIVEN
        val ex = ConflictException(Error())
        doThrow(ex).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        assertThrows<ConflictException> { consumer.consume(CreateWebpageListingCommand()) }
    }

    @Test
    fun `unsupported event`() {
        val result = consumer.consume(emptyMap<String, String>())

        assertFalse(result)
        verify(createWebpageListingCommandHandler, never()).handle(any())
    }
}
