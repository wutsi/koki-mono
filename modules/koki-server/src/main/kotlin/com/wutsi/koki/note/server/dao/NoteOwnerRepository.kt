package com.wutsi.koki.note.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteOwnerRepository : CrudRepository<NoteOwnerEntity, Long> {
    fun findByNoteId(noteId: Long): List<NoteOwnerEntity>

    fun findByNoteIdAndOwnerType(noteId: Long, ownerType: ObjectType): List<NoteOwnerEntity>
}
