package com.wutsi.koki.webscraping.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class WebscrapingMQConsumerTest {
    private val createWebpageListingCommandHandler = mock<CreateWebpageListingCommandHandler>()
    private val consumer = WebscrapingMQConsumer(
        createWebpageListingCommandHandler = createWebpageListingCommandHandler,
    )

    @Test
    fun `createWebpageListing successful`() {
        // GIVEN
        doReturn(true).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val cmd = CreateWebpageListingCommand()

        // THEN
        val result = consumer.consume(cmd)
        assertTrue(result)
        verify(createWebpageListingCommandHandler).handle(cmd)
    }

    @Test
    fun `createWebpageListing not successful`() {
        // GIVEN
        doReturn(false).whenever(createWebpageListingCommandHandler).handle(any())

        // WHEN
        val cmd = CreateWebpageListingCommand()

        // THEN
        val result = consumer.consume(cmd)
        assertFalse(result)
        verify(createWebpageListingCommandHandler).handle(cmd)
    }

    @Test
    fun testSupportedEvent() {
        val result = consumer.consume(emptyMap<String, String>())

        assertFalse(result)
        verify(createWebpageListingCommandHandler, never()).handle(any())
    }
}
