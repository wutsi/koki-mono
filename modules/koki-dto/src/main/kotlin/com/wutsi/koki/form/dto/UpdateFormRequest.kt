package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateFormRequest(
    @get:NotEmpty() @get:Size(max = 10) val code: String = "",
    @get:NotEmpty() @get:Size(max = 100) val name: String = "",
    var description: String? = null,
    var active: Boolean = true,
)
