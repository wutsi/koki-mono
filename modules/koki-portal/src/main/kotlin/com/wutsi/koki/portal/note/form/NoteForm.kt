package com.wutsi.koki.portal.note.form

import com.wutsi.koki.common.dto.ObjectType

data class NoteForm(
    val subject: String = "",
    val body: String = "",
    val ownerId: Long? = null,
    val ownerType: ObjectType = ObjectType.UNKNOWN,
)
