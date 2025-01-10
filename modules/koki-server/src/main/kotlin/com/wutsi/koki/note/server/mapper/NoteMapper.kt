package com.wutsi.koki.note.server.mapper

import com.wutsi.koki.note.dto.Note
import com.wutsi.koki.note.dto.NoteSummary
import com.wutsi.koki.note.server.domain.NoteEntity
import org.springframework.stereotype.Service

@Service
class NoteMapper {
    fun toNote(entity: NoteEntity): Note {
        return Note(
            id = entity.id!!,
            subject = entity.subject,
            body = entity.body,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
        )
    }

    fun toNoteSummary(entity: NoteEntity): NoteSummary {
        return NoteSummary(
            id = entity.id!!,
            subject = entity.subject,
            createdAt = entity.createdAt,
            body = entity.body,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
        )
    }
}
