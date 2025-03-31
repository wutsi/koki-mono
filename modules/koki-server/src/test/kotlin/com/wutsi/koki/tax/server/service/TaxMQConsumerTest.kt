package com.wutsi.koki.tax.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.note.dto.event.NoteCreatedEvent
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import com.wutsi.koki.note.server.service.NoteOwnerService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class TaxMQConsumerTest {
    private val taxService = mock<TaxService>()
    private val noteOwnerService = mock<NoteOwnerService>()
    private val logger = DefaultKVLogger()
    private val consumer = TaxMQConsumer(
        taxService = taxService,
        noteOwnerService = noteOwnerService,
        logger = logger
    )

    val noteId = 111L
    val taxId = 222L
    val tenantId = 555L
    private val owner = NoteOwnerEntity(noteId = noteId, ownerId = taxId, ownerType = ObjectType.TAX)

    @BeforeEach
    fun setUp() {
        doReturn(listOf(owner)).whenever(noteOwnerService).findByNoteIdAndOwnerType(noteId, ObjectType.TAX)
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `note created`() {
        consumer.consume(NoteCreatedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note updated`() {
        consumer.consume(NoteUpdatedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note deleted`() {
        consumer.consume(NoteDeletedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note not owner by TAX`() {
        doReturn(emptyList<NoteOwnerEntity>()).whenever(noteOwnerService)
            .findByNoteIdAndOwnerType(noteId, ObjectType.TAX)

        consumer.consume(NoteDeletedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService, never()).updateMetrics(any(), any())
    }

    @Test
    fun `event not supported`() {
        consumer.consume(FileUploadedEvent())
        verify(taxService, never()).updateMetrics(any(), any())
    }
}
