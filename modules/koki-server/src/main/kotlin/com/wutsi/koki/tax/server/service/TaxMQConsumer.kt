package com.wutsi.koki.tax.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.event.NoteCreatedEvent
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.service.NoteOwnerService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class TaxMQConsumer(
    private val taxService: TaxService,
    private val noteOwnerService: NoteOwnerService,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is NoteCreatedEvent) {
            onNoteCreated(event)
        } else if (event is NoteUpdatedEvent) {
            onNoteUpdated(event)
        } else if (event is NoteDeletedEvent) {
            onNoteDeleted(event)
        } else {
            return false
        }
        return true
    }

    private fun onNoteCreated(event: NoteCreatedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteUpdated(event: NoteUpdatedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteDeleted(event: NoteDeletedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteEvent(noteId: Long, tenantId: Long) {
        logger.add("event_note_id", noteId)
        logger.add("event_tenant_id", tenantId)

        val owners = noteOwnerService.findByNoteIdAndOwnerType(noteId, ObjectType.TAX)
        owners.forEach { owner ->
            logger.add("tax_id", owner.ownerId)
            taxService.updateMetrics(owner.ownerId, tenantId)
        }
    }
}
