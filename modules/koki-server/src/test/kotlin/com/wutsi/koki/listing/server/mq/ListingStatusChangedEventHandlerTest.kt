package com.wutsi.koki.listing.server.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.agent.ListingAgentFactory
import com.wutsi.koki.listing.server.service.agent.ListingDescriptorAgent
import com.wutsi.koki.listing.server.service.agent.ListingDescriptorAgentResult
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingPublisherTest {
    private val agentFactory = mock<ListingAgentFactory>()
    private val fileService = mock<FileService>()
    private val listingService = mock<ListingService>()
    private val objectMapper = ObjectMapper()
    private val logger = DefaultKVLogger()
    private val handler = ListingPublisher(
        agentFactory = agentFactory,
        fileService = fileService,
        listingService = listingService,
        objectMapper = objectMapper,
        logger = logger
    )

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        heroImageId = null,
        totalImages = null,
        totalFiles = null,
        status = ListingStatus.PUBLISHING
    )

    private val images = listOf(
        FileEntity(id = 111L, tenantId = tenantId),
        FileEntity(id = 222L, tenantId = tenantId),
        FileEntity(id = 333L, tenantId = tenantId)
    )

    private val agent = mock<ListingDescriptorAgent>()
    private val result = ListingDescriptorAgentResult(
        heroImageIndex = 1,
        title = "Hello",
        summary = "Hello world",
        description = "How are you",
        titleFr = "Bonjour",
        summaryFr = "Bonjour la gang",
        descriptionFr = "Comment ca va",
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(listing).whenever(listingService).save(any(), anyOrNull())

        doReturn(agent).whenever(agentFactory).createDescriptorAgent(any(), any())

        doReturn(
            ObjectMapper().writeValueAsString(result)
        ).whenever(agent).run(any(), any())

        doReturn(images).whenever(fileService).search(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), anyOrNull(), anyOrNull()
        )
        doReturn(File("/foo/bar")).whenever(fileService).download(any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onPublishing() {
        handler.handle(createEvent(ListingStatus.PUBLISHING))

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(ListingStatus.ACTIVE, listingArg.firstValue.status)
        assertEquals(images[1].id, listingArg.firstValue.heroImageId)
        assertEquals(result.title, listingArg.firstValue.title)
        assertEquals(result.summary, listingArg.firstValue.summary)
        assertEquals(result.description, listingArg.firstValue.description)
        assertEquals(result.titleFr, listingArg.firstValue.titleFr)
        assertEquals(result.summaryFr, listingArg.firstValue.summaryFr)
        assertEquals(result.descriptionFr, listingArg.firstValue.descriptionFr)

        val eventArg = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(eventArg.capture())
        assertEquals(ListingStatus.ACTIVE, eventArg.firstValue.status)
        assertEquals(listing.id, eventArg.firstValue.listingId)
        assertEquals(listing.tenantId, eventArg.firstValue.tenantId)
    }

    @Test
    fun `onPublishing - invalid listing status`() {
        doReturn(listing.copy(status = ListingStatus.ACTIVE))
            .whenever(listingService).get(any(), any())

        handler.handle(createEvent(ListingStatus.PUBLISHING))

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    private fun createEvent(status: ListingStatus): ListingStatusChangedEvent {
        return ListingStatusChangedEvent(
            status = status,
            listingId = listing.id!!,
            tenantId = listing.tenantId
        )
    }
}
