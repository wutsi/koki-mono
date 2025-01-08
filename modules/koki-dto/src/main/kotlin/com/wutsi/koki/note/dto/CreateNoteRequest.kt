
package com.wutsi.koki.note.dto

import jakarta.validation.constraints.NotEmpty

data class CreateNoteRequest(
    @get:NotEmpty val subject: String = "",
    @get:NotEmpty val body: String = "",
    val ownerId: Long? = null,
    val ownerType: String? = null,
)
