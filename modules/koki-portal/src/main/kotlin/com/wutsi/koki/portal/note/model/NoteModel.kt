package com.wutsi.koki.portal.note.model

import com.wutsi.koki.note.dto.NoteType
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class NoteModel(
    val id: Long = -1,
    val subject: String = "",
    val type: NoteType = NoteType.UNKNOWN,
    val summary: String = "",
    val body: String = "",
    val duration: Int = 0,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val modifiedAtMoment: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
) {
    val url: String
        get() = "/notes/$id"

    val durationMinutes: Int
        get() = duration % 60

    val durationHours: Int
        get() = duration / 60

    val durationText: String
        get() = durationToText()

    private fun durationToText(): String {
        val hh = durationHours
        val mm = durationMinutes
        val hhText = if (hh < 10) "0$hh" else "$hh"
        val mmText = if (mm < 10) "0$mm" else "$mm"

        return "$hhText:$mmText"
    }
}
