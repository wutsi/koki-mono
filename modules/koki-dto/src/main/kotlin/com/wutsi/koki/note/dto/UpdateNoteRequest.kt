package com.wutsi.koki.note.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateNoteRequest(
    @get:NotEmpty val subject: String = "",
    @get:NotEmpty val body: String = "",
)
