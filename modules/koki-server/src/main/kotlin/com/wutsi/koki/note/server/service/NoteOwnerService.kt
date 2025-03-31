package com.wutsi.koki.note.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.server.dao.NoteOwnerRepository
import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import org.springframework.stereotype.Service

@Service
class NoteOwnerService(private val dao: NoteOwnerRepository) {
    fun findByNoteIdAndOwnerType(noteId: Long, ownerType: ObjectType): List<NoteOwnerEntity> {
        return dao.findByNoteIdAndOwnerType(noteId, ownerType)
    }
}
