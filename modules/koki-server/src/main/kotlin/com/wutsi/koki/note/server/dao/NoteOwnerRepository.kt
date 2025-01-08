package com.wutsi.koki.note.server.dao

import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteOwnerRepository : CrudRepository<NoteOwnerEntity, Long> {
    fun findByNoteId(nodeId: Long): List<NoteOwnerEntity>
}
