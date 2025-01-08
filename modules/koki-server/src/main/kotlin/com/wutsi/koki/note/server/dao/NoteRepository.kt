package com.wutsi.koki.note.server.dao

import com.wutsi.koki.note.server.domain.NoteEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : CrudRepository<NoteEntity, Long>
