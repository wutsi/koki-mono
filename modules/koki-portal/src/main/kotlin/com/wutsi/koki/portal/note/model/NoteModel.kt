package com.wutsi.koki.portal.note.model

import com.wutsi.koki.portal.model.UserModel
import java.util.Date

data class NoteModel(
    val id: Long = -1,
    val subject: String = "",
    val body: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null
) {
    val url: String
        get() = "/notes/$id"
}
