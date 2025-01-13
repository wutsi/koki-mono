package com.wutsi.koki.portal.note.mapper

import com.wutsi.koki.note.dto.Note
import com.wutsi.koki.note.dto.NoteSummary
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.note.model.NoteModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class NoteMapper : TenantAwareMapper() {
    fun toNoteModel(
        entity: Note,
        users: Map<Long, UserModel>,
    ): NoteModel {
        val fmt = createDateFormat()
        return NoteModel(
            id = entity.id,
            subject = entity.subject,
            body = entity.body,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }

    fun toNoteModel(
        entity: NoteSummary,
        users: Map<Long, UserModel>,
    ): NoteModel {
        val fmt = createDateFormat()
        return NoteModel(
            id = entity.id,
            subject = entity.subject,
            body = entity.body,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }
}
