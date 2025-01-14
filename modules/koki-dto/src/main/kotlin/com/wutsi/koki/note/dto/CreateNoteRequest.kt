package com.wutsi.koki.note.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.NotEmpty

data class CreateNoteRequest(
    @get:NotEmpty val subject: String = "",
    @get:NotEmpty val body: String = "",
    val reference: ObjectReference? = null,
)
