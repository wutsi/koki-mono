package com.wutsi.koki.note.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.CreateNoteResponse
import com.wutsi.koki.note.dto.GetNoteResponse
import com.wutsi.koki.note.dto.SearchNoteResponse
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.note.dto.event.NoteCreatedEvent
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.mapper.NoteMapper
import com.wutsi.koki.note.server.service.NoteService
import com.wutsi.koki.platform.mq.Publisher
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/notes")
class NoteEndpoints(
    private val service: NoteService,
    private val mapper: NoteMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateNoteRequest,
    ): CreateNoteResponse {
        val note = service.create(request, tenantId)
        val response = CreateNoteResponse(note.id!!)
        publisher.publish(
            NoteCreatedEvent(
                noteId = note.id,
                tenantId = tenantId,
            )
        )
        return response
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateNoteRequest,
    ) {
        service.update(id, request, tenantId)
        publisher.publish(
            NoteUpdatedEvent(
                noteId = id,
                tenantId = tenantId,
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
        publisher.publish(
            NoteDeletedEvent(
                noteId = id,
                tenantId = tenantId,
            )
        )
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetNoteResponse {
        val note = service.get(id, tenantId)
        return GetNoteResponse(mapper.toNote(note))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchNoteResponse {
        val notes = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
        )
        return SearchNoteResponse(
            notes = notes.map { note -> mapper.toNoteSummary(note) }
        )
    }
}
