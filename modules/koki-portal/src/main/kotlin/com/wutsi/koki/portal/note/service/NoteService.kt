package com.wutsi.koki.portal.note.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.portal.note.form.NoteForm
import com.wutsi.koki.portal.note.mapper.NoteMapper
import com.wutsi.koki.portal.note.model.NoteModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiNotes
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val koki: KokiNotes,
    private val mapper: NoteMapper,
    private val userService: UserService,
) {
    fun note(id: Long): NoteModel {
        val note = koki.note(id).note

        // Users
        val userIds = listOf(note.createdById, note.modifiedById)
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            )
                .associateBy { user -> user.id }
        }

        return mapper.toNoteModel(
            entity = note,
            users = userMap
        )
    }

    fun notes(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<NoteModel> {
        val notes = koki.notes(
            ids = ids,
            ownerType = ownerType,
            ownerId = ownerId,
            limit = limit,
            offset = offset
        ).notes

        // Users
        val userIds = notes.flatMap { note ->
            listOf(note.createdById, note.modifiedById)
        }
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            )
                .associateBy { user -> user.id }
        }

        return notes.map { note ->
            mapper.toNoteModel(
                entity = note,
                users = userMap,
            )
        }
    }

    fun create(form: NoteForm): Long {
        val request = CreateNoteRequest(
            subject = form.subject,
            body = form.body,
            reference = form.ownerId?.let {
                ObjectReference(
                    id = form.ownerId,
                    type = form.ownerType
                )
            },
        )
        return koki.create(request).noteId
    }

    fun update(id: Long, form: NoteForm) {
        val request = UpdateNoteRequest(
            subject = form.subject,
            body = form.body,
        )
        return koki.update(id, request)
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
