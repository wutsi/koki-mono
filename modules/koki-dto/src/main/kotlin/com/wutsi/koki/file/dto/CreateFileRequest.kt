package com.wutsi.koki.file.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.NotEmpty

data class CreateFileRequest(
    @get:NotEmpty val url: String = "",
    val owner: ObjectReference? = null,
)
