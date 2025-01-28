package com.wutsi.koki.note.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateNoteRequest(
    @get:NotEmpty @get:Size(max = 255) val subject: String = "",
    @get:NotEmpty val body: String = "",
    val type: NoteType = NoteType.UNKNOWN,
    val reference: ObjectReference? = null,
    val duration: Int = 0,
)
