package com.wutsi.koki.portal.note.form

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.note.dto.NoteType

data class NoteForm(
    val subject: String = "",
    val body: String = "",
    val type: NoteType = NoteType.UNKNOWN,
    val durationHours: Int = 0,
    val durationMinutes: Int = 0,
    val ownerId: Long? = null,
    val ownerType: ObjectType = ObjectType.UNKNOWN,
)
