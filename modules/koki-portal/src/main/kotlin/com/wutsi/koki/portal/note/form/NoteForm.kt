package com.wutsi.koki.portal.note.form

data class NoteForm(
    val subject: String = "",
    val body: String = "",
    val ownerId: Long? = null,
    val ownerType: String? = null,
)
