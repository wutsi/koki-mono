package com.wutsi.koki.portal.note.service

import com.wutsi.koki.note.dto.SearchNoteResponse
import com.wutsi.koki.portal.note.mapper.NoteMapper
import com.wutsi.koki.portal.note.model.NoteModel
import com.wutsi.koki.portal.service.UserService
import com.wutsi.koki.sdk.KokiNotes
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val koki: KokiNotes,
    private val mapper: NoteMapper,
    private val userService: UserService,
) {
    fun notes(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: String? = null,
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
}
